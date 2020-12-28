function popup(name) {
    document.getElementById('modal1').style.display='block';
    $('#iframe1').attr('src','/reference/modules?name='+ name);
}

function close(id, sys_id, dv) {
    document.getElementById('modal1').style.display='none';

    var scope = angular.element($('#main-controller')).scope();
    scope.$apply(function () {
        scope.table.setReferenceField(id, sys_id, dv);
    });
}