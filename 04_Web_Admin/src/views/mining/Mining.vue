<template>
  <div>
      <Row>
          <Card>
                <p slot="title">
                矿机管理
                    <Button type="primary" size="small" @click="refreshPageManual">
                        <Icon type="refresh"></Icon>刷新
                    </Button>
                </p>

                <Row class="functionWrapper">
                    <div class="btnsWrapper clearfix">
                        <Button type="primary" @click="newActivity">新增矿机</Button>
                    </div>
                </Row>
                <Row>
                    <Table
                        :columns="columns"
                        :data="list"
                        border
                        :loading="ifLoading">
                    </Table>
                </Row>
                <div class="pageWrapper">
                    <Page
                        :total="pageNum"
                        :current="currentPageIdx"
                        @on-change="changePage"
                        show-elevator>
                    </Page>
                </div>
          </Card>

      </Row>
      <Modal
            class="auditModel"
            v-model="showProgressModel"
            title="请输入进度"
            width="550"
            @on-cancle="progress = ''"
            @on-ok="submitProgress">
            <Input v-model="progress" type="text" placeholder="请输入进度数值"></Input>
     </Modal>

     <Modal
            class="auditModel"
            v-model="showDetailModel"
            title="矿机产能详情"
            width="450">
            <Row>
                <H3 >矿机名：{{miningDetail.miningName}}</H3>
                 <span style="font-size:12px;" v-if="miningDetail.miningPeriod==0" >产出周期:日</span>
                 <span style="font-size:12px;" v-if="miningDetail.miningPeriod==1" >产出周期:周</span>
                 <span  style="font-size:12px;" v-if="miningDetail.miningPeriod==2" >产出周期:月</span>
                 <span style="font-size:12px;"  v-if="miningDetail.miningPeriod==3" >产出周期:年</span>
                 <br/>
                 <span style="font-size:12px;" >多少周期一次:{{miningDetail.miningDays}}</span>
                 <br/>
                 <span style="font-size:12px;">每周期单位量:{{miningDetail.miningDaysprofit}}</span>
                 <br/>
                 <span style="font-size:12px;">挖矿次数:{{miningDetail.miningTimes}}</span>
            </Row>
     </Modal>
  </div>
</template>

<script>

  import { getAllMining,updateShow, BASICURL } from '@/service/getData';
  import { getStore, removeStore, setStore } from '@/config/storage';

  export default{
    data () {
      return {
        miningDetail:{},               
        loginPassModal: false,
        showProgressModel: false,
        showDetailModel: false,
        progress: "",
        loginPW: '',
        activityForm:{
            title: "",
            detail: "",
            activityLink: "",
            noticeLink: "",
            settings: "",
            step: "0",
            type: "1",
            status: "0",
            imageUrl: ""
        },
        id: null,
        type: 0,
        ifShowPercentCircle: false,
        percentage: 0,
        picUrl: "",
        picUrlIcon: false,
        basicUrl: BASICURL,
        filterSearch: {
            pageNo: 1,
            pageSize: 10
        },
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        orderList: [],
        
        list: [],
        columns: [
          {
            title: '矿机名称',
            key:"miningName",
            width: 120
          },
           {
            title: '简介',
            key:"detail",
            width: 300
          },
          {
            title: '小图',
            width:130,
            render:(h, obj) => {
              let smallImageUrl = obj.row.smallImageUrl;
              return  h('img',{
                attrs: {src: smallImageUrl},
                style: {height: "45px", padding:"5px 0px"}
              },"");
            }
          },
          {
            title: '算力',
            key:"power",
            width:130
          },
          {
            title: '显示',
            width: 130,
            render: (h ,obj) => {
              let status =  obj.row.status;
              let txt = status == 0 ? "隐藏":"显示";
              let color = status == 1 ? "#19be6b":"#FF0000";
              return h('span',{
                style:{
                  color: color
                }
              },txt)
            }
          },
          
          {
            title: '发行价格',
            width: 130,
            key:"price"
          },
           {
            title: '接受币种',
            width: 130,
            key:"acceptUnit"
          },
           {
            title: '产出币种',
            width: 130,
            key:"miningUnit"
          },
         { title: '手续费',
            width: 100,
            key:"fee"},
          
          {title: '单人购买次数',
            key:"limitTimes",
            width: 120,
            },

          {
            title: '一级好友不能低于',
            key:"leveloneCount",
            width: 180,
          },
          {
              title: "操作",
              key: "xx",
              fixed: 'right',
              width: 280,
              render: (h, obj) => {
                let showProgress = "("+obj.row.progress+")";
                let disabled = true;                        
                return h("div", [
                                
                   h(
                    "Button",
                    {
                      props: {type: "warning",size: "small"},
                      style: {marginRight: "5px"},
                      on: {
                        click: () => {
                           this.HandleupdateShow(obj.row.id)
                        }
                      }
                    },
                    "更改显示状态"
                  ),                  
                  h(
                    "Button",
                    {
                      props: {type: "info",size: "small"},
                      style: {marginRight: "5px"},
                      on: {
                        click: () => {
                          removeStore('manageID');
                          setStore('manageID',  obj.row.id);
                          this.$router.push('/mining/mining/CreateMining');
                        }
                      }
                    },
                    "修改"
                  ),
                 
                  h(
                    "Button",
                    {
                      props: {type: "success",size: "small"},
                      style: {marginRight: "5px"},
                      on: {
                        click: () => {
                          this.showDetailModel = true;
                          this.miningDetail = obj.row
                        }
                      }
                    },
                    "矿机产能详情"
                  )
                ]);
              }
           }
        ]
      }
    },
    methods: {
   
      submitProgress(){
        let param = {};
        param["id"] = this.id;
        param["progress"] = this.progress;
        modifyActivityProgress(param).then( res => {
          if(!res.code) {
                this.showProgressModel = false;
                this.$Message.success("修改成功");
                this.refreshPage(1);
          }else{
                this.$Message.error(res.message)
          }
        })
      },
      HandleupdateShow(id){
           updateShow({id:id}).then(res =>{
                  if(!res.code) {              
                   this.$Message.success("修改成功");
                  }else{
                    this.$Message.error(res.message)              
                  }
                  this.refreshPage();
           })


      },
      newActivity(){
        removeStore('manageID');
        this.$router.push('/mining/mining/CreateMining');
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
        getAllMining(this.filterSearch).then( res => {
          if(!res.code) {
                this.ifLoading = false;
                this.pageNum = (res.data && res.data.totalElements) || 1;
                this.list = (res.data && res.data.content) || [];
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

<style scoped>
  .ivu-upload{
    display: inline-block;
  }
</style>
