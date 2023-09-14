console.log("this is script file");
const toggelSidebar = () =>{
   if($(".sidebar").is(":visible"))
   {
      //true
      //off sidebar
      $(".sidebar").css("display","none");
      $(".content").css("margin-left","0%");

   }
   else
   {
    //false
    //on sidebar 
       $(".sidebar").css("display","block");
       $(".content").css("margin-left","20%");
   }
};

const search=()=>{
       var query=$("#search-input").val();
       console.log(query);

       if(query=="")
       {
          $(".search-resulte").hide();
       }
       else
       {
         //sending request to serever
         let url=`http://localhost:8080/search/${query}`;
         
         //fetch
         fetch(url).then((res) => {
               return res.json();
         }).then((data) => {
               //data....
               console.log(data);

               //insert into html 
            let text=`<div class="list-group">`;
          
            data.forEach((contact) => {
            text +=`<a href='/user/contact-detail/${contact.cId}' class='list-group-item list-group-item-action'>${contact.name}</a>`;
            });
            text +=`</div>`;
            $(".search-resulte").html(text);
            $(".search-resulte").show();
         });
       }
};