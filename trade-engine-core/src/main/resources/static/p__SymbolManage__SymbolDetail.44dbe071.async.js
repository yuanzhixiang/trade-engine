(self.webpackChunkant_design_pro=self.webpackChunkant_design_pro||[]).push([[714],{14329:function(Z,i,n){"use strict";n.r(i);for(var x=n(81766),E=n(7935),j=n(99775),p=n(71947),F=n(89167),M=n(23643),N=n(44370),O=n(99957),S=n(59887),u=n(33523),I=n(80153),f=n(94043),c=n.n(f),e=n(67294),y=n(49500),l=n(53472),R=n(2839),U=n(57254),B=function(){var s=(0,e.useRef)();return e.createElement(l.Z,{actionRef:s,title:"\u6807\u7684\u8BE6\u60C5",request:(0,I.Z)(c().mark(function m(){return c().wrap(function(a){for(;;)switch(a.prev=a.next){case 0:return a.abrupt("return",Promise.resolve({success:!0,data:{id:123,date:"20200730",symbol:"ADJ89H"}}));case 1:case"end":return a.stop()}},m)}))},e.createElement(l.Z.Item,{label:"\u4E3B\u952E",dataIndex:"id"}),e.createElement(l.Z.Item,{label:"\u6807\u7684\u7F16\u7801",dataIndex:"symbol"}),e.createElement(l.Z.Item,{label:"\u521B\u5EFA\u65F6\u95F4",dataIndex:"date",valueType:"date"}))},T={admin:{name:"\u7BA1\u7406\u5458",desc:"\u4EC5\u62E5\u6709\u6307\u5B9A\u9879\u76EE\u7684\u6743\u9650"},operator:{name:"\u64CD\u4F5C\u5458",desc:"\u62E5\u6709\u6240\u6709\u6743\u9650"}},v=[],C=["\u9A6C\u5DF4\u5DF4","\u6D4B\u8BD5","\u6D4B\u8BD52","\u6D4B\u8BD53"],A=["\u5DF4\u5DF4","\u6D4B\u8BD5","\u6D4B\u8BD52","\u6D4B\u8BD53"],K=["baba@antfin.com","test@antfin.com","test2@antfin.com","test3@antfin.com"],L=["12345678910","10923456789","109654446789","109223346789"],b=[[],["\u6743\u9650\u70B9\u540D\u79F01","\u6743\u9650\u70B9\u540D\u79F04"],["\u6743\u9650\u70B9\u540D\u79F01"],[]],t=0;t<5;t+=1)v.push({outUserNo:"".concat(102047+t),avatar:"//work.alibaba-inc.com/photo/".concat(102047+t,".32x32.jpg"),role:t===0?"admin":"operator",realName:C[t%4],nickName:A[t%4],email:K[t%4],phone:L[t%4],permission:b[t%4]});var g=e.createElement(u.Z,null,e.createElement(u.Z.Item,{key:"admin"},"\u7BA1\u7406\u5458"),e.createElement(u.Z.Item,{key:"operator"},"\u64CD\u4F5C\u5458")),h=function(){var s=function(a){return e.createElement(O.Z,{key:"popconfirm",title:"\u786E\u8BA4".concat(a,"\u5417?"),okText:"\u662F",cancelText:"\u5426"},e.createElement("a",null,a))},m=[{dataIndex:"avatar",title:"\u6210\u5458\u540D\u79F0",valueType:"avatar",width:150,render:function(a,r){return e.createElement(M.Z,null,e.createElement("span",null,a),r.nickName)}},{dataIndex:"email",title:"\u8D26\u53F7"},{dataIndex:"phone",title:"\u624B\u673A\u53F7"},{dataIndex:"role",title:"\u89D2\u8272",render:function(a,r){return e.createElement(p.Z,{overlay:g},e.createElement("span",null,T[r.role||"admin"].name," ",e.createElement(U.Z,null)))}},{dataIndex:"permission",title:"\u6743\u9650\u8303\u56F4",render:function(a,r){var o=r.role,P=r.permission,d=P===void 0?[]:P;return o==="admin"?"\u6240\u6709\u6743\u9650":d&&d.length>0?d.join("\u3001"):"\u65E0"}},{title:"\u64CD\u4F5C",dataIndex:"x",valueType:"option",render:function(a,r){var o=s("\u9000\u51FA");return r.role==="admin"&&(o=s("\u79FB\u9664")),[e.createElement("a",{key:"edit"},"\u7F16\u8F91"),o]}}];return e.createElement(R.ZP,{columns:m,request:function(a,r,o){return console.log(a,r,o),Promise.resolve({data:v,success:!0})},rowKey:"outUserNo",pagination:{showQuickJumper:!0},toolBarRender:!1,search:!1})},W=function(){return e.createElement(y.Z,null,e.createElement(E.Z,null,e.createElement(B,null)),e.createElement("br",null),e.createElement(E.Z,null,e.createElement(h,null)))};i.default=W}}]);