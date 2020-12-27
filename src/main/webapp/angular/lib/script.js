function popup(name) {
    console.log(name);
    document.getElementById('modal1').style.display='block';
    $('#iframe1').attr('src','/reference/modules?name='+ name);
}

function close() {
    document.getElementById('modal1').style.display='none';
}