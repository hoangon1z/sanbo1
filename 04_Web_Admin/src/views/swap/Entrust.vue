<template>
  <div>
    <Card>
      <div slot="title" style="min-height:30px;width: 100%;">
        <div style="display:inline-block;float:left;" class="clearfix">
          <Button :type="btnType===0 ? 'primary' : 'ghost'" @click="localEnsure">当前委托</Button>
          <Button :type="btnType===1 ? 'primary' : 'ghost'" @click="hisEnsure" style="margin-left: 20px;">历史委托</Button>
        </div>
        <Button type="primary" @click="refreshPageManual" style="float:right;">
          <Icon type="refresh"></Icon>刷新
        </Button>
      </div>
      <Row class="priceSectionWrapper clearfix" >

        <div class="priceSection">价格区间：<Input v-model="filterSearch.minPrice"/> ~
          <Input v-model="filterSearch.maxPrice"/>
        </div>

        <div class="priceSection" style="margin-left:10px;">数量区间：<Input v-model="filterSearch.minAmount"/> ~
          <Input v-model="filterSearch.maxAmount"/>
        </div>

        <div class="orderStatus">
          <span>订单状态：</span>
          <Select v-model="filterSearch.status">
            <Option v-for="item in orderStatus"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>

        <div class="orderStatus">
          <span>订单方向：</span>
          <Select v-model="filterSearch.entrustType">
            <Option v-for="item in orderDirArr"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>

        <div class="orderStatus">
          <span>挂单类型：</span>
          <Select v-model="filterSearch.type" style="width:80px">
            <Option v-for="item in typeArr"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>
      </Row>
      <Row class="functionWrapper">
        <div class="searchWrapper clearfix" style="width:100%;">
          <Poptip trigger="hover" content="请输入 币单位 搜索" placement="bottom-start">
            <Input placeholder="请输入 币单位 搜索"
                  v-model="filterSearch.coinSymbol"
                  />
            </Input>
          </Poptip>

          <Poptip trigger="hover" content="请输入 委托单号 搜索" placement="bottom-start">
            <Input placeholder="请输入 委托单号 搜索"
                  v-model="filterSearch.contractOrderEntrustId"
                  />
            </Input>
          </Poptip>
          <Poptip trigger="hover" content="请输入 用户ID 搜索" placement="bottom-start">
            <Input placeholder="请输入 用户ID 搜索"
                  v-model="filterSearch.memberId"
                  />
            </Input>
          </Poptip>

          <div class="btns" style="float:right;">
            <Button type="info" @click="searchByFilter">搜索</Button>
          </div>
        </div>
      </Row>
			<Row>
        <Table
          :columns="columns_first"
          :data="userpage"
          :loading="ifLoading">
				</Table>
			</Row>

			<Row class="pageWrapper" >
				<Page :total="totalNum"
					style='margin-top:8px'
					:current="current"
					@on-change="changePage"
					show-elevator></Page>
			</Row>
    </Card>
  </div>

</template>

<script>

import dtime from 'time-formater'
import { queryContractEntrust ,cancelContractEntrust } from '@/service/getData';
import { setStore, getStore, removeStore } from '@/config/storage';

export default {
  data() {
    return {
	  btnType: 0,
      filterSearch: {
		coinSymbol: '',
        entrustType: '',
        type: '',
        orderId: '',
        memberId: '',
        baseSymbol: '',
        minPrice: '',
        maxPrice: '',
        minAmount: '',
        maxAmount: '',
        status: '',
        pageNo: 1,
				pageSize: 10,
				completed: 0 //0是委托订单1是历史订单
      },
      typeArr: [
        { status: 2, text: '限价' },
        { status: 3, text: '计划' },
        { status: '', text: '全部' }
      ],
      orderDirArr: [
        { status: 1, text: '买入开多' },
        { status: 2, text: '卖出开空' },
        { status: 3, text: '买入平空' },
        { status: 4, text: '卖出平多' },
        { status: '', text: '全部' }
      ],
      priceRange: '',
      orderStatusModel: null,
      //1:委托中/2:已撤销/3:委托失败/4:委托成功
      orderStatus: [
        { status: 1, text: '委托中' },
        { status: 2, text: '已撤销' },
        { status: 3, text: '委托失败' },
        { status: 4, text: '委托成功' },
        { status: '', text: '全部' },
      ],
      totalNum: null,
      current:　1,
      priceSecPass: false,
      priceLow: null,
      priceHeight: null,
      coinSymbol: null,
      direction: null,
      orderType: null,
      orderId: null,
      memberId: null,
      baseSymbol: null,
      currentPageIdx: 1,
      ifLoading: true,
      columns_first: [
        {
          title: "订单号",
          key: "contractOrderEntrustId",
          width: 180
				},
        {
          title: "用户ID",
          key: "memberId",
				},
        {
          title: "合约",
          key: "contractCoinName"
        },
        {
          title: "委托量(张)",
          key: "share"
        },
        {
          title: "杠杆",
          key: "leverage"
        },
        {
          title: "保证金",
          key: "principalAmount"
        },
        // {
        //   title: "成交量",
        //   key: "tradedAmount"
        // },
        {
          title: "挂单类型",
          key: "type",
          render: (h, params) => {
            const row = params.row;

            const type = row.type == "LIMIT_PRICE" ? "限价委托" : "计划委托";
            return h("span", {}, type);
          }
        },
        {
          title: "订单方向",
          key: "direction",
          render: (h, params) => {
            const row = params.row;
            const direction = (row.direction == "BUY"
                  ? row.entrustType == "OPEN"
                    ? "买入开多"
                    : "买入平空"
                  : row.entrustType == "OPEN"
                  ? "卖出开空"
                  : "卖出平多");
            return h("span", {}, direction);
          }
        },
        // {
        //   title: "触发价格",
        //   key: "triggerPrice",
        //   render: (h, params) => {
        //     const row = params.row;
        //     return h("span", {}, row.triggerPrice>0?row.triggerPrice:'');
        //   }
        // },
        {
          title: "委托价格",
          key: "entrustPrice",
          render: (h, params) => {
            const row = params.row;
            return h("span", {}, row.marketPrice?'对手价':row.entrustPrice);
          }
        },
        // {
        //   title: "触发时间",
        //   width: 100,
        //   render: (h, obj) => {
        //     let formatTime = obj.row.triggeringTime?dtime(obj.row.triggeringTime).format('YYYY-MM-DD HH:mm:ss'):''
        //     return h('span',{},formatTime)
        //   }
        // },
        {
          title: "挂单时间",
          width: 100,
          render: (h, obj) => {
            let formatTime = dtime(obj.row.createTime).format('YYYY-MM-DD HH:mm:ss')
            return h('span',{},formatTime)
          }
        },
        {
          title: "成交价格",
          key: "strikePrice",
          render: (h, params) => {
            const row = params.row;
            return h("span", {}, row.strikePrice || '');
          }
        },
        {
          title: "收益",
          key: "profit"
        },
        {
          title: "状态",
          key: "status",
          render: (h, params) => {
            const row = params.row;
            var direction = '';
            if(row.status == 'ENTRUST_ING'){
                direction = '委托中';
            }
            else if(row.status == 'ENTRUST_CANCEL'){
                direction = '已撤销'
            }
            else if(row.status == 'ENTRUST_FAILURE'){
                direction = '委托失败'
            }
            else if(row.status == 'ENTRUST_SUCCESS'){
                direction = '委托成功'
            }
            return h("span", {}, direction);
          }
        },
        {
          title: "操作",
          key: "age",
          width: 150,
          render: (h, obj) => {
            var actions = [];
            if(obj.row.status == 'ENTRUST_ING'){
             actions.push( h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.cancelContractEntrust(obj.row.id)
                    }
                  }
                },
                "撤销"));
            }
            return h("div", actions);
          }
        }
      ],
      userpage: []
    };
  },
  methods: {
		localEnsure() {
			this.filterSearch.pageNo = 1;
			this.current = 1;
			this.btnType = 0;
			this.filterSearch.completed = 0;
			this.refreshPage(this.filterSearch);
		},
		hisEnsure () {
			this.filterSearch.pageNo = 1;
			this.current = 1;
			this.btnType = 1;
			this.filterSearch.completed = 1;
			this.refreshPage(this.filterSearch);
		},
    searchByFilter() {
			let reg = /[^0-9]/;
      // alert(this.memberId)
      this.current = 1
			let bol = reg.test(this.memberId);
			if(bol&&(!!this.memberId)) {
				this.$Message.warning('请输入正确的id！')
				return;
			}

      if(isNaN(this.priceLow*1) ||　isNaN(this.priceHeight*1)) {
        this.$Message.warning('请输入正确的价格！')
      }else if(this.priceLow*1<0 || this.priceHeight*1<0) {
        this.$Message.warning('价格应该大于等于零！')
      } else {
        if(this.priceLow*1>this.priceHeight*1) {
          this.$Message.warning('最低价格不能高于最高价格')
        }else{
					this.$store.commit('switchLoading', true);
          this.refreshPage(this.filterSearch);
        }
      }
    },
    refreshPageManual() {
        for(let key in this.filterSearch)  {
            this.filterSearch[key] = '';
        }
        this.currentPageIdx = 1;
        this.current = 1;
        this.filterSearch.completed = this.btnType;
        this.filterSearch.pageNo = 1;
        this.filterSearch.pageSize = 10;
        this.refreshPage(this.filterSearch);
    },
   changePage(pageIndex) {
        this.current= pageIndex;
        this.filterSearch.pageNo = pageIndex;
        this.refreshPage(this.filterSearch);
    },
    refreshPage(obj) {
      this.ifLoading = true;
      console.log(obj)
      queryContractEntrust(obj).then(res => {
            this.userpage = res.data.content;
            this.totalNum = res.data.totalElements;
            this.ifLoading = false;
            this.$store.commit('switchLoading', false);
      });
    },
    cancelContractEntrust(id){
        cancelContractEntrust({ id:id }).then(res=>{
            if(res.code == 0) {
                this.$Message.success('撤销成功');
                this.refreshPage(this.filterSearch);
            }else{
                this.$Message.error('撤销失败');
            }
        })
        .catch(err => console.log(err))
    }
  },
  created() {
    this.refreshPage(this.filterSearch);
  }
}
</script>

<style lang="less" scoped>
.switchTab {
	margin: 20px 0;
}
Input,.ivu-select.ivu-select-single{
  width: 150px;
}
.priceSectionWrapper{
  margin-bottom: 10px;
  .ivu-input-wrapper.ivu-input-type {
    width: 60px ;
  }
  .priceSection {
    float: left;
  }
  .orderStatus {
    margin-left: 20px;
    float: left;
  }
  .btns{
    margin-left: 10px;
    float: left;
    height: 30px;
    line-height: 30px;
  }
}
.tips{
  color: red;
}
</style>
