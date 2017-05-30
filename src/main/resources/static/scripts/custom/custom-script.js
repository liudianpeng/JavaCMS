
document.getElementById("mainconfig").style.height = window.innerHeight + "px";
document.getElementById("settings").style.height = (window.innerHeight - 10) + "px";
document.getElementById("set_options").style.height = (window.innerHeight - 10) + "px";
document.getElementById("files").addEventListener("change",function (eventObject) {

    var selected_images = eventObject.target.files;
    var outputDiv = document.getElementById("options");
    outputDiv.innerHTML = "";
    for (var i = 0;i < selected_images.length;i++) {
        var img = document.createElement("img");
        var image_url = window.URL.createObjectURL(selected_images[i]);
        img.setAttribute("src",image_url);
       // img.setAttribute("width","100px");
       // img.setAttribute("height","100px");
        img.setAttribute("class","image-preview");
        outputDiv.appendChild(img);
    }
    for (var i=0;i<outputDiv.childNodes.length;i++) {
        outputDiv.childNodes[i].addEventListener("click",function (event) {
            var ckicked_image = event.target.src;
            var srcdoc = document.getElementById("iframe_preview").getAttribute("srcdoc");
            var new_srcdoc = srcdoc.replace("</body>","<style>body{ background-image: url("+ ckicked_image +")}</style></body>");
            document.getElementById("iframe_preview").setAttribute("srcdoc",new_srcdoc);
        });
    }
    var first_image = outputDiv.childNodes[0].getAttribute("src");
    console.log(first_image);
    var srcdoc = document.getElementById("iframe_preview").getAttribute("srcdoc");
    var new_srcdoc = srcdoc.replace("</body>","<style>body{ background-image: url("+ first_image +")}</style></body>");

    document.getElementById("iframe_preview").setAttribute("srcdoc",new_srcdoc);
});
document.getElementById("done_button").addEventListener("click",function (eventObject) {
    console.log(eventObject);
    console.log("Button clicked!");
    requestServer("imageUpload_siteBackground","none");
    //getContent();
});

function requestServer(jobName,value) {
    console.log("request");
    var xmlHttp = new XMLHttpRequest();
    // URL Example:
    // http://localhost:8080/admin/site-config/inComingMessage/jobName/?param=value
    var url = "https://localhost:8080/admin/site-config/inComingMessage";
    xmlHttp.onreadystatechange = function () {
        console.log(xmlHttp.readyState);
        if (xmlHttp.status == 200 && xmlHttp.readyState == 4){
                console.log("Ready:");
                console.log(xmlHttp.responseText);
                // document.getElementById("iframe_preview").setAttribute(
                //     "srcdoc",
                //     iframe_html_part1 +
                //     iframe_css_style_part1 +
                //     "body {" +
                //     "background-color: blue;" +
                //     "}" +
                //     iframe_css_style_part2 +
                //     iframe_html_part2 +
                //     xmlHttp.responseText +
                //     iframe_html_part3
                // );
        } else {

        }
    }
    xmlHttp.open("POST",url,true);
    if (jobName !== "imageUpload_siteBackground") {
        xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
    } else {
        //xmlHttp.setRequestHeader("Content-Type","multipart/form-data");
        var formData = new FormData(document.getElementById("form"));
        formData.append("job",jobName);
        formData.append("param","none");
        formData.append(document.getElementById("options").getAttribute("name"),document.getElementById("options").getAttribute("value"));
        console.log(formData);
    }
    xmlHttp.setRequestHeader("Accept","text/html");
    if (jobName !== "imageUpload_siteBackground") {
        xmlHttp.send("job="+jobName+"&param="+value);
    } else {
        xmlHttp.send(formData);
    }
}

var body = document.body.style;

var imageList = ['url("../../background-images/raven_1920x1080.jpg")',
    'url("../../background-images/fractal_flower1920x1080.jpg")',
"https://localhost:8080/siteconfig/backgrounds/4513271-background-images-hd.jpg"];

body.backgroundImage = imageList[2];
document.getElementById("settings").style.height = document.body.clientHeight + "px";
document.getElementById("settings").style.height = document.body.clientWidth / 2 + "px";
document.getElementById("set_options").style.height = document.body.clientHeight + "px";
document.getElementById("set_options").style.height = document.body.clientWidth / 2 + "px";
window.addEventListener("resize",function () {
    console.log(document.body.clientWidth +"px auto, cover");
    body.backgroundSize =  document.body.clientWidth + "px auto, contain";
    body.backgroundRepeat = "no-repeat";
});
var iframe_html_part1 = "<!DOCTYPE html><html><head>";
var iframe_css_style_part1 = "<style>";
var iframe_css_style_part2 = "</style>";
var iframe_script_part1 = "<script>";
var iframe_script_part2 = "</script>";
var iframe_html_part2 = "</head><body>";
var iframe_html_part3 = "</body></html>";
//document.getElementById("iframe_preview").setAttribute("srcdoc",html);
//http://localhost:8080/admin/site-config/inComingMessage/getHTML/?param=index
function indexAjax() {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.status == 200 && xmlHttp.readyState == 4) {
            document.getElementById("iframe_preview").setAttribute("srcdoc",xmlHttp.responseText);
        }
    };
    xmlHttp.open("GET","https://localhost:8080",true);
    xmlHttp.send();
}
indexAjax();
function getContent() {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.status == 200 && xmlHttp.readyState == 4) {
            console.log(xmlHttp.getAllResponseHeaders());
            var binaryData = [];
            binaryData.push();
            var blob = new Blob(binaryData);
            var img = document.createElement("img");
            console.log(window.URL.createObjectURL(blob));
        }
    };
    xmlHttp.open("GET","https://localhost:8080/admin/site-config/content/content_type=images&user=admin&dir=adminDir",true);
    xmlHttp.setRequestHeader("Content-Type","text/plain");
    xmlHttp.setRequestHeader("Accept","*/*");
    xmlHttp.send();
}


// function base64ToArrayBuffer(base64) {
//     var binary_string =  window.atob(base64);
//     var len = binary_string.length;
//     var bytes = new Array( len );
//     for (var i = 0; i < len; i++)        {
//         bytes[i] = binary_string.charCodeAt(i);
//     }
//
//     var blob = new Blob(bytes, {type : 'image/jpeg'});
//
//     var blobURL = window.URL.createObjectURL(blob);
//     var reader = new FileReader();
//     var result = reader.readAsDataURL(blob);
//     reader.addEventListener("load", function () {
//         preview.src = reader.result;
//     }, false);
//
//     if (file) {
//         reader.readAsDataURL(file);
//     }
//
//     //return bytes.buffer;
// }
// // base64ToArrayBuffer(stringBase64);
// //var bytesS = base64ToArrayBuffer(stringBase64);
// //var blob = new Blob(bytesS, {type : 'image/jpeg'});
// //console.log(blob);
