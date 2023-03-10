<template>
  <div>
    <Card>
      <p slot="title">
        合约账单明细
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button>
      </p>

      <Row class="functionWrapper">
				<div class="searchWrapper clearfix">

					<div class="poptip">
						<Poptip trigger="hover"
										content="请输入会员ID"
										placement="bottom-start">
							<Input placeholder="请输入会员ID"
										v-model="filterSearch.memberId"/>
							</Input>
						</Poptip>
					</div>

					<!-- <div class="poptip">
						<Poptip trigger="hover"
										content="请输入会员昵称"
										placement="bottom-start">
							<Input placeholder="请输入会员昵称"
										v-model="filterSearch.account"/>
							</Input>
						</Poptip>
					</div> -->

					<div class="poptip">
						<span>交易类型：</span>
						<Select v-model="filterSearch.type">
							<Option v-for="(item, index) in tradeTypeArr"
										:value="index"
										:key="item">{{ item }}</Option>
						</Select>
					</div>
					<br>
					<div class="poptip range">
						<Poptip trigger="hover"
										content="最低交易金额搜索"
										placement="bottom-start">
							<Input placeholder="最低交易金额搜索"
										v-model="filterSearch.minMoney"/>
							</Input>
						</Poptip>
						~
						<Poptip trigger="hover"
										content="最高交易金额搜索"
										placement="bottom-start">
							<Input placeholder="最高交易金额搜索"
										v-model="filterSearch.maxMoney"/>
							</Input>
						</Poptip>
					</div>
					<div class="poptip">
						<DatePicker
							type="daterange"
							placement="bottom-end"
							@on-change="dateRange"
							placeholder="选择时间区间">
						</DatePicker>
					</div>

					<div class="btns">
						<Button type="info" size="small" @click="searchByFilter">搜索</Button>
					</div>

				</div>
			</Row>

      <Row class="tableWrapper">
        <Table :columns="columns_first" :data="userpage" border :loading="ifLoading"></Table>
      </Row>

      <Row class="pageWrapper" >
        <Page :total="pageNum" :current="currentPageIdx" @on-change="changePage" show-elevator></Page>
      </Row>

    </Card>
  </div>
</template>

<script>
import {contractDetails} from '@/service/getData';
  export default{
    data () {
      return {
		filterSearch: {
			pageNo: 1,
			pageSize: 10,
			memberId: '',
			type: '',
			minMoney: '',
			maxMoney: '',
			minFee: '',
			maxFee: '',
			startTime: '',
			endTime: ''
		},
		tradeTypeArr: { 
			'IN':'转入', 
			'OUT':'转出', 
			'CONUT':'结算', 
			'CLOSE':'强平'
		},
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        userpage: [],
        columns_first: [
          {
            title: '会员ID',
            key:"memberId"
          },
          {
            title: '交易类型',
            render: (h ,obj) => {
              let type =  obj.row.type;
              return h('span',{
              },this.tradeTypeArr[type])
            }
          },
          {
            title: '交易金额',
            render: (h ,obj) => {
              let  amount =  obj.row.amount;
              let  symbol =  obj.row.symbol;
              return h('span',{},amount+' '+symbol)
            }
          },
          {
            title: '交易时间',
            key:"createTime"
          },
        ]
      }
    },
    methods: {
			searchByFilter() {
				let reg = /\D/;
				if(reg.test(this.filterSearch.memberId)) {
					this.$Message.warning('请输入正确的会员ID!');
					return;
				}
				this.currentPageIdx = 1;
				this.filterSearch.pageNo = 1;
				this.refreshPage(this.filterSearch)
			},
			dateRange(val) {
				this.filterSearch.startTime = val[0];
				this.filterSearch.endTime = val[1];
			},
      refreshPageManual() {
				this.currentPageIdx = 1;
				for(let key in this.filterSearch) {
					this.filterSearch[key] = '';
				}
				this.filterSearch.pageNo = 1;
				this.filterSearch.pageSize = 10;
				this.refreshPage(this.filterSearch);
      },
      changePage(pageIndex) {
				this.currentPageIdx = pageIndex;
				this.filterSearch.pageNo = pageIndex;
				this.refreshPage(this.filterSearch)
      },
      refreshPage(obj) {
				this.ifLoading = true;
        contractDetails(obj).then( res => {
          if(!res.code) {
						this.ifLoading = false;
						this.pageNum = (res.data && res.data.totalElements) || 1;
						this.userpage =res.content
          }else{
						this.$Message.error(res.message)
					}
        })
      }
    },
    created () {
      this.refreshPage();
    }
  }
</script>

<style lang="less" scoped>

</style>
