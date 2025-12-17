import { defineStore } from 'pinia'
import request from '@/utils/request'
import { useUserStore } from '@/stores/useUserStore'

export const usePageStore = defineStore('page', {
    // 加载数据的方法
    state: () => ({
        pageNum: 1,
        pageSize: 5,
        total: 0,
        tableData: [],
        name: '',
        nurseName: '',
        type: '',
        title: '',
        record: '',
    }),
    actions: {
        setType(newType) {
            this.type = newType,
                this.reset();//切换类型时重置分页状态
        },
        reset() {
            this.pageNum = 1;
            this.pageSize = 5;
            this.total = 0;
            this.tableData = [];
            this.name = '';
            this.nurseName = '';
            this.title = '';
            this.record = '';
        },
        async loadData() {
            //校验类型
            if (!this.type) {
                console.error('请先调用 setType 设置数据类型');
                return
            }

            // 获取当前登录用户，用于自动附加筛选参数（患者/护工）
            const userStore = useUserStore()
            const user = userStore.userInfo || {}

            //动态调用接口
            const apiMap = {
                admin: '/admin/selectPage',
                nurse: '/nurse/selectPage',
                patient: '/patient/selectPage',
                ward: '/ward/selectPage',
                nurseService: '/nurseService/selectPage',
                careKnowledge: '/careKnowledge/selectPage',
                hp: '/hp/selectPage',
                nurseReserve: '/nurseReserve/selectPage',
                serviceReserve: '/serviceReserve/selectPage',
                evaluation: '/evaluation/selectPage',
                dailyMonitor: '/dailyMonitor/selectPage',
            }
            const apiUrl = apiMap[this.type]
            if (!apiUrl) {
                console.error('无效的数据类型：', this.type)
                return
            }

            try {
                // 基础分页/搜索参数
                const params = {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    name: this.name,
                    nurseName: this.nurseName,
                    title: this.title,
                    record: this.record,
                }
                // 在构造请求参数处加入按页面类型判断的护工过滤逻辑
                const nurseFilteredPages = new Set([
                    'dailyMonitor',   // 日常监测页
                    'nurseReserve',   // 护工预约页（按需修改为你项目里实际的 type 名称）
                    'serviceReserve', // 护理服务页
                    'evaluation',     // 评价页
                ]);

                // 自动按角色附加过滤参数
                if (user && user.role === 'PATIENT') {
                    // 患者只能查看自己的数据
                    params.patientId = user.id;
                }

                // 只在明确需要按护工过滤的页面（type）时，为 NURSE 添加 nurseId 过滤
                if (user && user.role === 'NURSE' && nurseFilteredPages.has(this.type)) {
                    params.nurseId = user.id;
                }

                const res = await request.get(apiUrl, { params })
                // 兼容多种后端返回形态：PageInfo (data.list + data.total) 或 直接返回数组
                if (res && res.data) {
                    if (res.data.list && Array.isArray(res.data.list)) {
                        this.tableData = res.data.list
                        this.total = Number(res.data.total ?? res.data.list.length)
                    } else if (Array.isArray(res.data)) {
                        this.tableData = res.data
                        this.total = res.data.length
                    } else {
                        // 兜底：如果后端返回单个对象时包成数组
                        this.tableData = res.data ? [res.data] : []
                        this.total = this.tableData.length
                    }
                } else {
                    this.tableData = []
                    this.total = 0
                }
            } catch (error) {
                console.error('加载数据失败', error)
            }
        },
        // 单独更新页码（用于页码切换）
        setPageNum(newPage) {
            this.pageNum = newPage;
            this.loadData();
        },
        // 单独更新每页条数（用于条数切换）
        setPageSize(newSize) {
            this.pageSize = newSize;
            this.pageNum = 1;
            this.loadData();
        },
        // 更新搜索参数（用于搜索框输入后查询）
        setName(newName) {
            this.name = newName;
            this.pageNum = 1;
            this.loadData();
        },
        // 更新搜索参数（用于搜索框输入后查询），标题
        setTitle(newTitle) {
            this.title = newTitle;
            this.pageNum = 1;
            this.loadData();
        },
        // 更新搜索参数（用于搜索框输入后查询）,日期
        setRecord(newrecord) {
            this.record = newrecord;
            this.pageNum = 1;
            this.loadData();
        }
    }
})