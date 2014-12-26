angular.module('app').service('WorkSheetDayService', function ($http) {

    this.save = function (workSheetDay) {
        return $http.post('/worksheets/day/save', workSheetDay)
            .success(function (result) {
                return result;
            });
    }

    this.list = function (structureId) {
        return $http.get('/worksheets/day/json/list', {params: {'structureId': structureId}}).then(function (result) {
            return result.data;
        });
    }

});



