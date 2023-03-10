<template>
  <div>
    <Card>
      <div slot="title" style="min-height:30px;width: 100%;">
        <Button type="primary" @click="refreshPageManual" style="float:right;">
          <Icon type="refresh"></Icon>刷新
        </Button>
      </div>
      <Row class="priceSectionWrapper clearfix" >
        <div class="orderStatus">
          <span>申请状态：</span>
          <Select v-model="filterSearch.status">
            <Option v-for="item in applyStatus"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>
      </Row>
      <Row class="functionWrapper">
        <div class="searchWrapper clearfix" style="width:100%;">
          <Poptip trigger="hover" content="请输入 众筹标题 搜索" placement="bottom-start">
            <Input placeholder="请输入 众筹标题 搜索"
                  v-model="filterSearch.fundingTitle"
                  />
            </Input>
          </Poptip>

          <Poptip trigger="hover" content="请输入 众筹id 搜索" placement="bottom-start">
            <Input placeholder="请输入 众筹id 搜索"
                  v-model="filterSearch.orderId"
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
     <Modal
          class="auditModel"
          v-model="donateRatioModel"
          title="请先设置" 
          @on-ok="submitPass"         
          >
          <Form v-model="donateRatioForm" :label-width="140"> 
             <FormItem label="输入捐赠返KICK比率:"  >
              <Input v-model="donateRatioForm.donateRatio" ></Input>
          </FormItem>
          <FormItem label="输入提现手续费比率:"  >
              <Input v-model="donateRatioForm.cashRatio" ></Input>
          </FormItem>
          </Form>        
           <Span><b>说明:</b>返KICK比率公式为：返KICK金额/捐赠金额。 比如某人捐100 返80kick 那么这个比率就是0.8
           </br> 手续费比率: 比如提现100 扣除2kb  这个比率就是0.02
           </Span>
                               
     </Modal>  
     <Modal
          class="auditModel"
          v-model="detailModel"
          title="捐助记录"
          >
          <span>总被捐助次数:{{detailTimes}}</span>
           <Table
          :columns="columns_detail"
          :data="detailData"
          :loading="ifLoading">
				</Table>
     </Modal>   
      <Modal
          class="auditModel"
          v-model="picModel"
          title="其他资料"
          >
         
           <Table
          :columns="pic_detail"
          :data="picData"
          :loading="ifLoading">
				</Table>
     </Modal>  
        <Modal
          class="auditModel"
          v-model="crInfo"
          title="众筹信息"
          >   
     <SPan><b>发起人:</b>{{crowInfo.sponsor}}</SPan>
     <br/>
     <SPan><b>众筹标题:</b>{{crowInfo.fundingTitle}}</SPan>
     <br/>
     <Span><b>求助说明:</b>{{crowInfo.instructions}}</Span>
     <br/>
     <Span><b>目标金额:</b>{{crowInfo.targetAmount}}</Span>
     <br/>
     <Span><b>收到金额:</b>{{crowInfo.amountReceived}}</Span>
     <br/>
     <Span v-if="crowInfo.documentType==1"><b>证件类型:</b>身份证 <br/></Span>
     <Span v-if="crowInfo.documentType==2"><b>证件类型:</b>护照 <br/></Span>
     
     <Span><b>证件号:</b>{{crowInfo.documentNumber}}</Span>
     <br/>
     
     <Span><b>图片</b> </Span>
     <br/>
     <img :src=crowInfo.picUrl style="height:200px"/>
      
           <Table
          :columns="pic_detail"
          :data="picData"
          :loading="ifLoading">
				</Table>
  </Modal> 
  </div>

</template>

<script>

import dtime from 'time-formater'
import { queryCrowdList,passCrow,refusesCrow,getDetail,getPicDetail } from '@/service/getData';
import { setStore, getStore, removeStore } from '@/config/storage';

export default {
  data() {
    return {
     
      detailModel:false,
			btnType: 0,
      filterSearch: {
        id: '',
        memberId: '',
        status: '',
        fundingTitle:'',
        pageNo: 1,				
        pageSize: 10,				
      }, 
        detailSerch: {
        pageNo: 1,			
        pageSize: 10,				
      }, 
      applyStatus: [
        { status: 0, text: '未审核' },
        { status: 1, text: '已通过' },
        { status: 2, text: '未通过' },
      ],
      donateRatioModel:false,
      donateRatioForm:{donateRatio:0.8,  cashRatio:0.02},
      
      currentDetail:1,
      detailData:[],
      totalNum: null,
      current:　1,
      priceSecPass: false,
      priceLow: null,
      priceHeight: null,
      coinSymbol: null,
      orderDirection: null,
      orderType: null,
      orderId: null,
      memberId: null,
      baseSymbol: null,
      currentPageIdx: 1,
      ifLoading: true,
      times:0,
      crowInfo:{},
      crInfo:false,
      picModel:false,
      picData:[],
      pic_detail:[
          {
          title: '其他资料',
          width: 485 ,
          /*  标题 */
          
          /*  键值 */
          key: 'picUrl',
          render: (h, params) => {
            return h("img", {
              /*  组件样式 */
              style: {
                width: "250px",
               
                "border-radius": "5%"
              },
              /*  html属性 */
              attrs: {
                /*  图片的路径,直接采用后台返回的键值 */
                src: params.row.picUrl,
                
              }
            });
          }
        },
      ],
      columns_first: [
        {
          title: "众筹id",
          key: "id",
          width: 80
          },
         {
          title: "众筹信息详情",
          width:120,
           render: (h, obj) => {
            var actions = [];
             actions.push(h(
                "Button",
                {
                  props: {
                    type: "success",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.crowdDatil(obj.row)
                    }
                  }
                },
                "查看"));
                 return h("div", actions);
        },
         },
        {
          title: "被求助次数",
          key: "times",
          width:130
         },				
        {
          title: "发起人",
          key: "sponsor",
          width:120
        },
        {
          title: "用户id",
          key: "memberId",
          width:80
        },
        {
          title: '图片',
          width: 120 ,
          /*  标题 */
          
          /*  键值 */
          key: 'picUrl',
          render: (h, params) => {
            return h("img", {
              /*  组件样式 */
              style: {
                width: "110px",
                height: "110px",
                "border-radius": "5%"
              },
              /*  html属性 */
              attrs: {
                /*  图片的路径,直接采用后台返回的键值 */
                src: params.row.picUrl,
                
              }
            });
          }
        },
          {
          /*  标题 */
          title: '其他资料',
          width:120,
           render: (h, obj) => {
            var actions = [];
             actions.push(h(
                "Button",
                {
                  props: {
                    type: "success",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.picDatil(obj.row)
                    }
                  }
                },
                "查看"));
                 return h("div", actions);
        },
        },
          {
          width:160,
          title: "返KICK费率",
          key: "donateRatio",
         },	
        
         {
          title: "申请时间",
          width: 160,
          render: (h, obj) => {
            let formatTime = dtime(obj.row.addTime).format('YYYY-MM-DD')
            return h('span',{},formatTime)
          }
        },      
           {
          title: "通过时间",
          width: 160,
          render: (h, obj) => {
            if(obj.row.passTime!=null){
            let passTime = dtime(obj.row.passTime).format('YYYY-MM-DD')
            return h('span',{},passTime)
            }                  
            
          }
        }, 
         {
          title: "结束时间",
          width: 160,
          render: (h, obj) => {
            if(obj.row.endTime!=null){
            let endTime = dtime(obj.row.endTime).format('YYYY-MM-DD ')
            return h('span',{},endTime)
            } 
        }
         },
        {
          title: "众筹类型",
          width: 160,
          key: "type",
          render: (h, params) => {
            const row = params.row;         
            if(row.type===1){
                const type="心愿众筹"
                return h("span", {}, type);
            }
            if(row.type===2){
                const type="创业众筹"
                return h("span", {}, type);
            }        
          }
        },
          {
          title: "状态",
          key: "type",
          width:160,
          render: (h, params) => {
            const row = params.row;
             if(row.status===0){
                const type="待审核"
                 return h("span", {}, type);
            }         
            if(row.status===1){
                const type="已通过"
                 return h("span", {}, type);
            }
            if(row.status===2){
                const type="未通过"
                 return h("span", {}, type);
            }  
             if(row.status===3){
                const type="已过期"
                 return h("span", {}, type);
            }   
          }
        },   
        {
          title: "操作",
          width: 160,
          render: (h, obj) => {
            var actions = [];
            if(obj.row.status === 0){
             actions.push(h(
                "Button",
                {
                  props: {
                    type: "success",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.handlePassCrow(obj.row.id)
                    }
                  }
                },
                "通过"));
             actions.push(h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      console.log(this)
                      this.handleRefuseCrow(obj.row.id)
                    }
                  }
                },
                "拒绝"));
            }
            if(obj.row.status === 1){
             actions.push(h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.handledetail(obj.row.id)
                    }
                  }
                },
                "捐助记录"));
            }
            return h("div", actions);
          }
        }
      ],
      detailTimes:0,
      userpage: [],
      columns_detail:[ {
          title: "用户id",
          key: "memberId"
        },
        {title: "用户名",
          key: "memberName"
        },
         {title: "捐助金额",
          key: "fundingMoney"
        },
         {title: "捐助时间",
          width: 150,
           render: (h, obj) => {
            if(obj.row.fundingTime!=null){
            let fundingTime = dtime(obj.row.fundingTime).format('YYYY-MM-DD HH:mm:ss')
            return h('span',{},fundingTime)
            } 
        }
        },
        
        ]
    };
  },
  methods: {
		// switchEnsure() {

		// },
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
   this.refreshPage(this.filterSearch);
        
      
    },
    refreshPageManual() {
     for(let key in this.filterSearch)  {
      this.filterSearch[key] = '';
		}
      this.currentPageIdx = 1;
      this.current = 1;
      this.btnType = 0;
      this.filterSearch.pageNo = 1;
      this.filterSearch.pageSize = 10;
      this.filterSearch.completed = 0;
      this.refreshPage(this.filterSearch);
    },
   changePage(pageIndex) {
      this.current= pageIndex;
      this.filterSearch.pageNo = pageIndex;
      this.refreshPage( this.filterSearch);
    },


    refreshPage(obj) {
      this.ifLoading = true;
      queryCrowdList(obj).then(res => {
        this.userpage = res.data.content;
        this.totalNum = res.data.totalElements;
        this.ifLoading = false;
        this.$store.commit('switchLoading', false);
      });
    },
     crowdDatil(row){
       let type = row.type  
        let id = row.id 
         getPicDetail({id:id,type:type}).then(res =>{
          this.picData = res.data

      })
      this.crInfo = true;
      this.crowInfo = row
    },
    handlePassCrow(id){
      this.donateRatioModel =true
      this.passCorwdId = id   
	
  
    },
    submitPass(){    
      let donateRatio = this.donateRatioForm.donateRatio
      let id = this.passCorwdId
      let cashRatio =  this.donateRatioForm.cashRatio
      //alert(donateRatio)
      passCrow({id:id,donateRatio:donateRatio,cashRatio:cashRatio}).then(res=>{
		if(res.code === 0){
		this.$Message.success('审核通过成功');
		this.refreshPage(this.filterSearch);
                                                                                }                                        else{
    this.$Message.error('审核通过失败:'+res.message);
    this.donateRatioModel = true
		}
			})
		.catch(err => console.log(err))

    },
    picDatil(row){
        this.picModel = true;
        let type = row.type  
        let id = row.id 
      getPicDetail({id:id,type:type}).then(res =>{
          this.picData = res.data

      })

    },

     handleRefuseCrow(id){
                      console.log(id)
                refusesCrow({id:id}).then(res=>{
		if(res.code === 0){
		this.$Message.success('审核拒绝成功');
		this.refreshPage(this.filterSearch);
                                                                                }
                                        else{
		this.$Message.error('审核拒绝失败');
		}
			})
		.catch(err => console.log(err))
                      
  },
   handledetail(id){
      this.detailModel = true     
      getDetail({detailSerch:this.detailSerch,id:id}).then(res => {
        this.detailData = res.data.data.content
        this.detailTimes = res.data.count
        this.ifLoading = false;
        this.$store.commit('switchLoading', false);
      })
      .catch(err => console.log(err)) ;
                      
  },
    
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
