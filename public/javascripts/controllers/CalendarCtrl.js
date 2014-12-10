angular.module('app').controller('CalendarTypeCtrl', function ($scope, $filter, calendarTypesData, dayTypesData, CalendarTypeService, CalendarService, $modal, $log) {
    $scope.calendarTypes = calendarTypesData;
    $scope.dayTypes = dayTypesData;

    $scope.resetCalendarTypeForm = function () {
        $scope.newCalendarTypeForm = {
            id: 0,
            name: null,
            created_at: '2011-01-01 00:00:00',
            updated_at: '2011-01-01 00:00:00'
        }
    }

    $scope.weekDays = {
        Monday: 'Понедельник',
        Tuesday: 'Вторник',
        Wednesday: 'Среда',
        Thursday: 'Четверг',
        Friday: 'Пятница',
        Saturday: 'Суббота',
        Sunday: 'Воскресенье'
    }

    $scope.yearMonths = {
        '01': 'Январь',
        '02': 'Февраль',
        '03': 'Март',
        '04': 'Апрель',
        '05': 'Май',
        '06': 'Июнь',
        '07': 'Июль',
        '08': 'Август',
        '09': 'Сентябрь',
        '10': 'Октябрь',
        '11': 'Ноябрь',
        '12': 'Декабрь'
    }

    $scope.years = [];

    $scope.filterByMonth = function (criteria) {
        return function (item) {
            if (isNaN(criteria))
                return true;
            var month = new Date((Date.parse(item.calendar_date))).getMonth() + 1;
            return month === parseInt(criteria);
        };
    };

    $scope.searchCalendar = function () {
        $scope.calendar
        CalendarService.getByTypeAndYear($scope.calendarForm.type, $scope.calendarForm.year).then(function (days) {
            $scope.calendarDays = [];
            $scope.calendarDays = days;
        });
    }

    $scope.delete = function (item, dayType) {
        alert((item.calendar_date) + ' ' + dayType.name);
    };

    $scope.selectedCalendarType = {};

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

    $scope.rowClass = function (calendarDay) {
        switch (calendarDay.day_type) {
            case 1:
                return "";
            case 2:
                return "info";
            case 3:
                return "warning";
            case 4:
                return "danger";
        }
    }

    $scope.calendarTypeChanged = function (calendarTypeId) {
        if (isNaN(calendarTypeId)) {
            $scope.years = [];
            return;
        }

        $scope.calendarDays = [];
        $scope.calendarForm.year = null;
        CalendarTypeService.getTypeYears(calendarTypeId).then(function (years) {
            $scope.years = years;
        });


    }

    $scope.updateCalendarDay = function (calendarDay, dayType) {

        if (calendarDay.day_type == dayType.id)
            return;

        calendarDay.day_type = dayType.id;

        CalendarService.update(calendarDay).then(function (result) {
            PNotify.desktop.permission();
            (new PNotify({
                title: 'Статус обновления',
                text: 'Запись успешно обновлена',
                desktop: {
                    desktop: true
                }
            })).get().click(function (e) {
                    if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                });
        });
    }

    $scope.openCalendarTypeModal = function (size) {

        var modalInstance = $modal.open({
            templateUrl: 'calendarTypeModal.html',
            controller: 'ModalCalendarTypeController',
            size: size
        });

        modalInstance.result.then(function (result) {
            $scope.calendarTypes.push(result.data);
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

    $scope.openCalendarModal = function (size) {

        var modalInstance = $modal.open({
            templateUrl: 'calendarModal.html',
            controller: 'ModalCalendarController',
            size: size,
            resolve: {
                calendarTypeData: function () {
                    return $scope.calendarForm.type;
                }
            }

        });

        modalInstance.result.then(function (result) {
            $scope.years.push(result);
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

});


angular.module('app').controller('ModalCalendarTypeController', function ($scope, $modalInstance, CalendarTypeService) {

    $scope.newCalendarTypeForm = {
        id: 0,
        name: '',
        created_at: '2014-01-01 00:00:00',
        updated_at: '2014-01-01 00:00:00'
    };

    $scope.saveCalendarType = function () {

        $scope.ok(CalendarTypeService.save($scope.newCalendarTypeForm));

        $scope.newCalendarTypeForm = {
            id: 0,
            name: '',
            created_at: '2014-01-01 00:00:00',
            updated_at: '2014-01-01 00:00:00'
        };
    }

    $scope.ok = function (result) {
        $modalInstance.close(result);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('app').controller('ModalCalendarController', function ($scope, $modalInstance, CalendarService, calendarTypeData) {


    $scope.newCalendarForm = {
        calendarType: calendarTypeData,
        year: ''
    };

    $scope.saveCalendar = function () {
        $scope.ok(CalendarService.updateByTypeAndYear($scope.newCalendarForm.calendarType, $scope.newCalendarForm.year));

    };

    $scope.ok = function (result) {
        $modalInstance.close(result);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

