angular.module('app').controller('CalendarTypeCtrl', function ($scope, $filter, calendarTypesData, CalendarTypeService, $modal, $log) {
    $scope.calendarTypeList = calendarTypesData;

    $scope.resetCalendarTypeForm = function () {
        $scope.newCalendarTypeForm = {
            id: 0,
            name: null,
            created_at: '2011-01-01 00:00:00',
            updated_at: '2011-01-01 00:00:00'
        }
    };

    $scope.delete = function (item) {
        alert(item.email);
    };

    $scope.selectedCalendarType = {};

    // example data
    $scope.list = [
        {
            "id": 0,
            "name": "Frazier Bush",
            "gender": "male",
            "email": "frazierbush@avit.com",
            "phone": "+1 (995) 523-3013"
        },
        {
            "id": 1,
            "name": "Justice Watson",
            "gender": "male",
            "email": "justicewatson@avit.com",
            "phone": "+1 (839) 451-3734"
        },
        {
            "id": 2,
            "name": "Macdonald Burks",
            "gender": "male",
            "email": "macdonaldburks@avit.com",
            "phone": "+1 (820) 598-2459"
        },
        {
            "id": 3,
            "name": "Natalia Velasquez",
            "gender": "female",
            "email": "nataliavelasquez@avit.com",
            "phone": "+1 (867) 507-3895"
        },
        {
            "id": 4,
            "name": "Diana Bond",
            "gender": "female",
            "email": "dianabond@avit.com",
            "phone": "+1 (817) 433-2736"
        },
        {
            "id": 5,
            "name": "Keller May",
            "gender": "male",
            "email": "kellermay@avit.com",
            "phone": "+1 (934) 567-3253"
        }
    ];

    $scope.resetCalendarTypeForm();

    $scope.saveCalendarType = function () {
        $scope.newCalendarTypeForm.id = 0;

        CalendarTypeService.save($scope.newCalendarTypeForm).then(function (result) {
            $scope.calendarTypeList.push(result.data);
            PNotify.desktop.permission();
            (new PNotify({
                title: 'Статус сохранения',
                text: 'Новая запись успешно сохранена.',
                desktop: {
                    desktop: true
                }
            })).get().click(function (e) {
                    if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                });
        });

        $scope.resetCalendarTypeForm();
    }

    $scope.eventSources = [];

    $scope.uiConfig = {
        calendar: {
            height: 450,
            editable: true,
            header: {
                left: 'title',
                center: '',
                right: 'today prev,next'
            },
            eventClick: $scope.alertOnEventClick,
            eventDrop: $scope.alertOnDrop,
            eventResize: $scope.alertOnResize,
            eventRender: $scope.eventRender
        }
    };
});


