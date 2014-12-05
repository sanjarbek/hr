angular.module('app').service('CalendarTypeService', function ($http) {

    this.save = function (calendarType) {
        return $http.post('/calendar_types/save', calendarType)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/calendar_types/json/list').then(function (result) {
            return result.data;
        });
    }

});




