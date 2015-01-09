angular.module('app').controller('ContactCtrl', function ($scope, $filter, contactInformationData, ContactInformationService, $modal, $log) {

    if (contactInformationData != "null") {
        $scope.contactInformationForm = contactInformationData;
        $scope.isNew = false;
    } else {
        $scope.contactInformationForm = {
            id: 0,
            employee_id: null,
            home_address: null,
            living_address: null,
            email: null,
            home_phone: null,
            mobile_phone: null,
            created_at: '2014-01-01T00:00:00',
            updated_at: '2014-01-01T00:00:00'
        };
        $scope.isNew = true;
    }

    $scope.editMode = true;

    $scope.resetContactInformation = function () {
        $scope.selectAction = $scope.saveContactInformation;
        $scope.isCollapsed = false;
        $scope.contactInformationForm = {
            id: 0,
            employee_id: null,
            home_address: null,
            living_address: null,
            email: null,
            home_phone: null,
            mobile_phone: null,
            created_at: '2014-01-01T00:00:00',
            updated_at: '2014-01-01T00:00:00'
        };
    }

    $scope.saveContactInformation = function () {

        if ($scope.isNew) {
            $scope.contactInformationForm.id = 0;
            $scope.contactInformationForm.employee_id = $scope.activeEmployee.id;

            ContactInformationService.save($scope.contactInformationForm).then(function (result) {

                $scope.isNew = false;
                $scope.contactInformationForm.id = result.data.id;

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

        } else {
            ContactInformationService.update($scope.contactInformationForm).then(function (result) {

                $scope.isNew = false;
                PNotify.desktop.permission();
                (new PNotify({
                    title: 'Статус изменений',
                    text: 'Изменения успешно сохранены.',
                    desktop: {
                        desktop: true
                    }
                })).get().click(function (e) {
                        if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                    });
            });
        }

        $scope.editMode = !$scope.editMode;
    };

})
