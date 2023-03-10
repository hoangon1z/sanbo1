<template>
    <div>
        <Card>
            <p slot="title">
                意见反馈
                <Button type="primary" size="small" @click="refreshPageManual">
                    <Icon type="refresh"></Icon>
                    刷新
                </Button>
            </p>
            <Row class="tableWrapper">
                <Table :columns="columns" :data="list" border :loading="ifLoading">
                </Table>
            </Row>
            <Row class="pageWrapper">
                <Page :total="total" :current="currentPageIdx" @on-change="changePage" show-elevator></Page>
            </Row>

        </Card>
    </div>
</template>

<script>
import { feedback } from '@/service/getData';
export default {
    data() {
        return {
            list: [],
            total: 0,
            realName: "",
            pageNo: 1,
            mobilePhone: "",
            email: "",
            currentPageIdx: 1,
            ifLoading: true,
            columns: [
                {
                    title: "编号",
                    key: "id"
                },
                {
                    title: '用户名',
                    render: (h, params) => {
                        return h("div", {}, params.row.member.username);
                    }
                },
                {
                    title: '反馈意见',
                    key: 'remark'
                },
                {
                    title: "创建时间",
                    key: "createTime"
                }
            ]
        }
    },
    methods: {
        init() {
            feedback().then(res => {
                if (!res.code) {
                    this.ifLoading = false;
                    this.list = res.data.content;
                    this.total = res.data.totalElements;
                } else {
                    this.$Message.error(res.message);
                }
            })
        },
        refreshPageManual() {
            this.currentPageIdx = 1;
            this.pageNo = 1;
            this.init();
        },
        search() {
            this.pageNum = this.currentPageIdx = 1;
            this.init();
        },
        changePage(index) {
            this.pageNo = this.currentPageIdx = index;
            this.init();
        }
    },
    created() {
        this.init();
    }
}
</script>

<style lang="less" scoped>
</style>
