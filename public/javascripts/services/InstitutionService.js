angular.module('app').service('InstitutionService', function ($http) {

    this.save = function (institution) {
        return $http.post('/institutions/save', institution)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/institutions/json/list').then(function (result) {
            return result.data;
        });
    }

});

angular.module('app').service('QualificationService', function ($http) {

    this.save = function (qualification) {
        return $http.post('/qualification_types/save', qualification)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/qualification_types/json/list').then(function (result) {
            return result.data;
        });
    }

});

angular.module('app').service('EducationService', function ($http) {

    this.save = function (institution) {
        return $http.post('/educations/save', institution)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/educations/json/list').then(function (result) {
            return result.data;
        });
    }

    this.update = function (education) {
        return $http.put('/educations/update', education)
            .success(function (result) {
                return result;
            })
    }

    this.delete = function (education) {
        console.log(education);
        return $http.delete('/educations/delete', {params: {id: education.id}})
            .success(function (result) {
                return result;
            })
    }
});