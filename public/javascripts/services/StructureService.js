angular.module('app').service('StructureService', function ($http) {

    this.save = function (structure) {
        return $http.post('/structures/save', structure)
            .success(function (result) {
                return result;
            });
    }

    this.update = function (structure) {
        return $http.put('/structures/update', structure)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/structures/json/list').then(function (result) {
            return result.data;
        });
    }

    this.freepositions = function () {
        return $http.get('/structures/json/freepositions').then(function (result) {
            return result.data;
        });
    }

    this.get = function (id) {
        return $http.get('/structures/json/get', {params: {'structureId': id}}).then(function (result) {
            return result.data;
        });
    }

});

angular.module('app').service('StructureTypeService', function ($http) {

    this.save = function (structure_type) {
        return $http.post('/structure_types/save', structure_type)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/structure_types/json/list').then(function (result) {
            return result.data;
        });
    }

});


