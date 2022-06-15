$(document).ready(function () {
    /*$.ajax({
        url:"http://192.168.1.95:8080",
        type: "get", 
        error: function (xhr, status) {
            console.log(status);
        },
        success: function(data) {
           console.log(data);
           data = JSON.parse(data);
        }
    });*/
    /*function httpGet(theUrl)
    {
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "GET", theUrl, false ); // false for synchronous request
        xmlHttp.send( null );
        return xmlHttp.responseText;
    }

    console.log(httpGet("http://127.0.0.1:8080/api/data"));*/

    var xhr = new XMLHttpRequest();
    xhr.open( "GET", "http://127.0.0.1:8080/api/data" );
    //OMIT THESE...
    //xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    //xhr.withCredentials = true;
    //xhr.setRequestHeader( 'Content-Type', _contenttype );
    //xhr.setRequestHeader( 'CrossDomain', 'true' );
    //....

    xhr.addEventListener( 'load',  function () {
        xhr.responseJSON = JSON.parse( xhr.responseText );
        alert( xhr.responseJSON);
    });

    xhr.onerror = function(e) { 
        alert("Ajax request error");
    };

    xhr.send( JSON.stringify({}) );
})