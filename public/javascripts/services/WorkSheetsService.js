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

    this.generate = function (structureId) {
        return $http.get('/worksheets/day/generate', {params: {'structureId': structureId}}).then(function (result) {
            return result.data;
        });
    }

    this.changeWorkDayDayType = function (workSheetDay, dayType) {
        return $http.put('/worksheets/day/updateDayType', {'sheetDayId': workSheetDay.id, 'dayTypeId': dayType.id}).then(function (result) {
            return result.data;
        });
    }

    this.changeWorkDayHours = function (workSheetDay, hours) {
        return $http.put('/worksheets/day/updateHours', {'sheetDayId': workSheetDay.id, 'hours': hours}).then(function (result) {
            return result.data;
        });
    }

});



