angular.module('app').service('PositionService', function ($http) {

    this.save = function (position) {
        $http.post('/positions/save', position)
            .success(function (result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/positions/json/list').then(function (result) {
            return result.data;
        });
    }

});

angular.module('app').service('PositionCategoryService', function ($http) {

    this.save = function (position_category) {
        return $http.post('/position_categories/save', position_category)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/position_categories/json/list').then(function (result) {
            return result.data;
        });
    }

});

