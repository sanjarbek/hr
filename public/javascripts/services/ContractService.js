angular.module('app').service('ContractTypeService', function ($http) {

    this.save = function (contract_type) {
        $http.post('/contract_types/save', contract_type)
            .success(function (result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/contract_types/json/list').then(function (result) {
            return result.data;
        });
    }

});

