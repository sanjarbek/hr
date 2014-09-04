angular.module('app').service('RelationshipService', function ($http, EmployeeService) {
    //to create unique relationship id
    var uid = 1;

    //relationships array to hold list of all relationships
    var relationships = [];

    //save method create a new relationship if not already exists
    //else update the existing object
    this.save = function (relationship) {
        $http.post('/relationships/save', relationship)
            .success(function (relationship) {
                PNotify.desktop.permission();
                (new PNotify({
                    title: 'Статус сохранения',
                    text: 'Успешно сохранен.',
                    desktop: {
                        desktop: true
                    }
                })).get().click(function (e) {
                        if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                        alert('Hey! You clicked the desktop notification!');
                    });
                console.log(relationship);
                dyn_notice();
                function dyn_notice() {
                    var percent = 0;
                    var notice = new PNotify({
                        title: "Please Wait",
                        type: 'info',
                        icon: 'glyphicon glyphicon-eye-open',
                        hide: false,
                        buttons: {
                            closer: false,
                            sticker: false
                        },
                        opacity: .75,
                        shadow: false
//                        width: "270px"
                    });

                    setTimeout(function () {
                        notice.update({
                            title: false
                        });
                        var interval = setInterval(function () {
                            percent += 2;
                            var options = {
                                text: percent + "% complete."
                            };
                            if (percent == 80) options.title = "Almost There";
                            if (percent >= 100) {
                                window.clearInterval(interval);
                                options.title = "Done!";
                                options.type = "success";
                                options.hide = true;
                                options.buttons = {
                                    closer: true,
                                    sticker: true
                                };
                                options.icon = 'picon picon-task-complete';
                                options.opacity = 1;
                                options.shadow = true;
                                options.width = PNotify.prototype.options.width;
                            }
                            notice.update(options);
                        }, 120);
                    }, 2000);
                }
            });
    }

    //simply search contacts list for given id
    //and returns the relationship object if found
    this.get = function (id) {
        for (i in relationships) {
            if (relationships[i].id == id) {
                return relationships[i];
            }
        }

    }

    //iterate through relationships list and delete
    //contact if found
    this.delete = function (id) {
        for (i in relationships) {
            if (relationships[i].id == id) {
                relationships.splice(i, 1);
            }
        }
    }

    //simply returns the relationships list
    this.list = function (id) {
        return $http.get('/employees/json/family', { params: {'employeeId': id}}).then(function (result) {
            return result.data;
        });
    }
});

angular.module('app').service('RelationshipTypeService', function ($http) {

    //save method create a new relationship if not already exists
    //else update the existing object
    this.save = function (relationship_type) {
        $http.post('/relationship_types/save', relationship_type)
            .success(function (relationship_type) {
                console.log(relationship_type);
            });
    }

    //simply returns the relationship_types list
    this.list = function () {
        return $http.get('/relationship_types/json/list').then(function (result) {
            return result.data;
        });
    }
});

