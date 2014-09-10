angular.module('app').service('RelationshipService', function ($http, EmployeeService) {

    //relationships array to hold list of all relationships
    var relationships = [];

    //save method create a new relationship if not already exists
    //else update the existing object
    this.save = function (relationship) {
        return $http.post('/relationships/save', relationship)
            .success(function (result) {
                return result;
//                dyn_notice();
//                function dyn_notice() {
//                    var percent = 0;
//                    var notice = new PNotify({
//                        title: "Please Wait",
//                        type: 'info',
//                        icon: 'glyphicon glyphicon-eye-open',
//                        hide: false,
//                        buttons: {
//                            closer: false,
//                            sticker: false
//                        },
//                        opacity: .75,
//                        shadow: false
////                        width: "270px"
//                    });
//
//                    setTimeout(function () {
//                        notice.update({
//                            title: false
//                        });
//                        var interval = setInterval(function () {
//                            percent += 2;
//                            var options = {
//                                text: percent + "% complete."
//                            };
//                            if (percent == 80) options.title = "Almost There";
//                            if (percent >= 100) {
//                                window.clearInterval(interval);
//                                options.title = "Done!";
//                                options.type = "success";
//                                options.hide = true;
//                                options.buttons = {
//                                    closer: true,
//                                    sticker: true
//                                };
//                                options.icon = 'picon picon-task-complete';
//                                options.opacity = 1;
//                                options.shadow = true;
//                                options.width = PNotify.prototype.options.width;
//                            }
//                            notice.update(options);
//                        }, 120);
//                    }, 2000);
//                }
            });
    }

    this.update = function (relationship) {
        return $http.put('/relationships/update', relationship)
            .success(function (result) {
                return result;
            })
    }

    this.delete = function (relationship) {
        console.log(relationship);
        return $http.delete('/relationships/delete', {params: {id: relationship.id}})
            .success(function (result) {
                return result;
            })
    }

    //simply returns the relationships list
    this.list = function (id) {
        return $http.get('/employees/json/family', { params: {'employeeId': id}}).then(function (result) {
            return result.data;
        });
    }
});

angular.module('app').service('RelationshipTypeService', function ($http) {

    this.save = function (relationship_type) {
        return $http.post('/relationship_types/save', relationship_type)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/relationship_types/json/list').then(function (result) {
            return result.data;
        });
    }
});

