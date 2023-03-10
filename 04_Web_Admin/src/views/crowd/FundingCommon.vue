<template>
                    

  <div>
      <Row>
          <Card>
                <p slot="title">
                   众筹公益评论            
                </p>
                <div style="margin-bottom : 10px">
                <!-- <Button type="success" size="big" @click="handleAddLevel">
                        <Icon type="add"></Icon>新增会员等级
                </Button>  -->
                </div>
                <Row>
                    <Table
                        :columns="columns"
                        :data="orderList"
                        border
                        :loading="ifLoading"
                        >
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


  </div>
</template>

<script>
  
import dtime from 'time-formater'
import { conmonList, passCheck, delineCheck } from '@/service/getData';
 
  export default{
    data () {
      const self = this;
      return {                
        orderList:[],
        btnEvent: "",
        btnEventString: "",
        loginPW: '',        
        filterSearch: {
            pageNo: 1,
            pageSize: 10
        },
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        columns: [
          {
            title: "会员id",
            key: "memberId",
            minWidth: 80,
          },
           {
            title: "评论众筹id",
            key: "fundingId",
            minWidth: 80,
          },
          {
            title: "会员名",
            key: "memberName",
            minWidth: 120,
          },
          
          {
            title: "评论",
            key: "common",
            minWidth: 300,
          },
             {
          title: "评论时间",
          width: 150,
          render: (h, obj) => {
            let commonTime = dtime(obj.row.commonTime).format('YYYY-MM-DD HH:mm:ss')
            return h('span',{},commonTime)
          }
        },
           {
          width: 120,
          title: "众筹类型",
          key: "fundingType",
          render: (h, params) => {
            const row = params.row;         
            if(row.fundingType==1){
                const type="心愿众筹"
                 return h("span", {}, type);
            }
            if(row.fundingType==2){
                const type="创业众筹"
                 return h("span", {}, type);
            }    
            if(row.fundingType==3){
                const type="医疗众筹"
                 return h("span", {}, type);
            }                    
          }        
        },
          {                       
          title: "状态",
          key: "status",
          width:80,      
          render: (h, params) => {
            const row = params.row;         
            if(row.status==0){
                const type="待审核"
                 return h("span", {}, type);
            }
            if(row.status==1){
                const type="已通过"
                 return h("span", {}, type);
            }   
             if(row.status==2){
                const type="已屏蔽"
                 return h("span", {}, type);
            }       
          }
        },

         {
          title: "操作",
          width: 200,
          render: (h, obj) => {           
            var actions = [];
            if(obj.row.status == 0){
             actions.push(h(
                "Button",
                {
                  props: {
                    type: "success",
                    size: "small",
                    
                  },
                   style:{   
                    margin:' 10px',
 
                  },
                  on: {
                    click: () => {
                       this.handlePass(obj.row.id)
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
                      this.handleDeline(obj.row.id)
                     
                    }
                  }
                },
                "屏蔽"));
            }
            
           
            return h("div",
             actions);
          }
        }
        ]
      };
    },



    methods: {

      changePage(pageIndex) {
                this.currentPageIdx = pageIndex;
                this.filterSearch.pageNo = pageIndex;
                this.refreshPage(this.filterSearch)
      },

      handlePass(id){
           passCheck({id:id}).then(res =>{ 
                   this.$Message.success("通过成功")
                   this.refreshPage();         
             }).catch(err =>{
                  this.$Message.error(res.message)
             })                                   
      },
      handleDeline(id){
           delineCheck({id:id}).then(res =>{ 
                   this.$Message.success("屏蔽成功")
                   this.refreshPage();         
             }).catch(err =>{
                  this.$Message.error(res.message)
             }) 
      },
      refreshPage(obj) {
        this.ifLoading = true;
        conmonList(this.filterSearch).then( res => {
          if(!res.code) {
                this.ifLoading = false;
                this.pageNum = (res.data && res.data.totalElements) || 1;
                this.orderList = (res.data && res.data.content) || [];
          }else{
                this.$Message.error(res.message)
          }
        })
      },
      closeDetail(){
        this.showDetailModal = false;
      },
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
