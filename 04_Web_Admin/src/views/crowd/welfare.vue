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
          <Poptip trigger="hover" content="请输入 公益标题 搜索" placement="bottom-start">
            <Input placeholder="请输入 公益 搜索"
                  v-model="filterSearch.fundingTitle"
                  />
            </Input>
          </Poptip>

          <Poptip trigger="hover" content="请输入 公益id 搜索" placement="bottom-start">
            <Input placeholder="请输入 公益id 搜索"
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
          :loading="ifLoading"
          >
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
          v-model="passModal"
          title="通过审核"
          @on-ok="handleSubmit"
          >  
        <i-form :model="formItem" :label-width="80">
        <Form-item label="请输入放款金额"  prop="money">
            <i-input  v-model="formItem.money"  number placeholder="请输入"></i-input>
        </Form-item>  
         </i-form>
      </Modal>

      
      <Modal
          class="auditModel"
          v-model="refuseModal"
          title="拒绝"
          @on-ok="refuce"
          >  
        <i-form :model="refuseFormItem" :label-width="100">
        <Form-item label="请输入拒绝理由">
            <i-input v-model="refuseFormItem.text" placeholder="请输入"></i-input>
        </Form-item>  
        </i-form>
      </Modal>

      <Modal
          class="auditModel"
          v-model="instructDatiel"
          title="线下公益说明"
          >  

      <sPan>{{this.instruct}} </sPan>    
      </Modal>
           <Modal
          class="auditModel"
          v-model="picModel"
          title="图片资料"
          >
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
import { queryWelfare,passWelfare,refuceWelfare,getPicDetail} from '@/service/getData';
import { setStore, getStore, removeStore } from '@/config/storage';
 
export default {
  data() {
    return {
      formItem:{money:'',id:'',mid:'',maxMoney:""},                  
      patianInfo:false,
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
      instruct:"",
      refuseModal:false,
      refuseFormItem:{
           text:"",
           wid:"",
      },
      instructDatiel:false,
      picModel:false,
      picData:[],
      pic_detail:[
          {
          title: '图片资料',
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
     
      passModal:false,
      paInfo:{ 
      patientName:'',
      patientDocumentType:'',
      patientDocumentNumber:'',
      visitInformation:'',
      houseProperty:'',
      carProperty:'',
      income:'',
      property:'',
      debt:'',
      
      otherFundType:'',
      },   
      crInfo:false,
      crowInfo:{},

      columns_first: [
        {
          title: "众筹id",
          key: "id",
          width: 80},
        {
          width:140,  
          title: "发起人",
          key: "sponsor",
        },
         {
          width:140,
          title: "标题",
          key: "fundingTitle",
         },
         {
          title: "说明", width:120,
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
                      this.instruDatil(obj.row.instructions)
                    }
                  }
                },
                "查看"));
                 return h("div", actions);
        },
          
         },
         {
          title: "申请金额",
          key: "applyAmount",
          width:130
         },
         
          {
          /*  标题 */
          title: '图片',
          width:140,
          /*  键值 */
          key: 'picUrl',
          align: "center",
          render: (h, params) => {
            return h("img", {
              /*  组件样式 */
              style: {
                width: "100px",
                height: "100px",
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
          title: '其他材料',
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
          title: "证件类型",
          key: "documentType",
          width:100,
          render: (h, params) => {
            const row = params.row;         
            if(row.documentType===1){
                const type="身份证"
                 return h("span", {}, type);
            }
            if(row.documentType===2){
                const type="护照"
                 return h("span", {}, type);
            }        
          }
        },
        {
         
          title: "证件号",
          key: "documentNumber",
          width:250,
          
        },
          {
         
          title: "收到金额",
          key: "amountReceived",
          width:100,
          
        },  
           {
          title: "通过时间",
          width: 100,
          render: (h, obj) => {
            if(obj.row.passTime!=null){
            let passTime = dtime(obj.row.passTime).format('YYYY-MM-DD')
            return h('span',{},passTime)
            }                  
            
          }
        }, 
          {
            width: 100,
          title: "申请状态",
          key: "type",
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
          }
        },      
        {
          title: "操作",
          width: 200,
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
                      this.handlePassCrow(obj.row)
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
        picDatil(row){
        this.picModel = true;
        let type = 4 
        let id = row.id 
      getPicDetail({id:id,type:type}).then(res =>{
          this.picData = res.data

      })

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
      queryWelfare(obj).then(res => {
        this.userpage = res.data.content;
        this.totalNum = res.data.totalElements;
        this.ifLoading = false;
        this.$store.commit('switchLoading', false);
      });
    },
    instruDatil(instruct){
          this.instruct =instruct
          this.instructDatiel = true

    },
    handleRefuseCrow(id){
         this.refuseFormItem.wid = id
         this.refuseModal = true
         //(this.refuseFormItem.wid)
    },
    handlePassCrow(row){
                    this.formItem.id = row.id 
                    this.formItem.mid = row.memberId                       
                    this.passModal = true
                    this.formItem.maxMoney = row.applyAmount
    },
   
   refuce(){
     // alert(this.refuseFormItem.text)
       const wid =  this.refuseFormItem.wid
       const text =  this.refuseFormItem.text
      refuceWelfare({wid:wid,text:text}).then(res => { 
          if(res.code==0){ this.$Message.success('审核拒绝成功');
         }     
         else{
                  this.$Message.success('审核拒绝失败');           
         }  
         this.refreshPage(this.filterSearch);           
         this.refuseModal = false 
      })
      .catch(err => console.log(err));    
       this.refreshPage(this.filterSearch);
       this.refuseModal = false 
   },

     
  handleSubmit(){
           //alert(this.formItem.maxMoney)             
         const mid =  this.formItem.mid
         const wid =  this.formItem.id 
         const money = this.formItem.money 
         let reg = /[^0-9]/;
      // alert(this.memberId)
      
      let bol = reg.test(this.formItem.money);
	if(bol&&(!!this.formItem.money)) {
	this.$Message.warning('请输入整数！')
	return;
              }
      if(money> this.formItem.maxMoney ){
                    this.$Message.warning('不可大于申请金额')
	return;      
      }    else{
                          
         passWelfare({mid:mid, wid:wid,money:money}).then(res => { 
          if(res.code==0){ this.$Message.success('审核通过成功');
         }     
         else{
                  this.$Message.success('审核通过失败');           
         }  
         this.refreshPage(this.filterSearch);           
         this.passModal = false 
      })
      .catch(err => console.log(err));    
       this.refreshPage(this.filterSearch);
       this.passModal = false 
      }    
   
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
