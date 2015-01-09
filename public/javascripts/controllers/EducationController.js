angular.module('app').controller('EducationController', function ($scope, $filter, educationsData, qualificationsData, institutionsData, EducationService, $modal, $log) {
    $scope.educations = educationsData;
    $scope.institutions = institutionsData;
    $scope.qualifications = qualificationsData;

    $scope.isCollapsed = true;


    $scope.saveEducation = function () {
        $scope.educationForm.id = 0;
        $scope.educationForm.employee_id = $scope.activeEmployee.id;

        EducationService.save($scope.educationForm).then(function (result) {
            $scope.educations.push(result.data);
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

        $scope.educationForm = {};
    }

    $scope.resetEducation = function () {
        $scope.selectAction = $scope.saveEducation;
        $scope.isCollapsed = false;
        $scope.educationForm = {
            id: null,
            employee_id: null,
            institution_id: null,
            qualification_id: null,
            speciality: null,
            end_date: null,
            serialnumber: null
        };
    }

    $scope.selectEducation = function (education) {
        $scope.selectAction = $scope.updateEducation;
        $scope.educationForm = education;
        $scope.educationForm.start_date = $filter("date")(education.start_date, 'yyyy-MM-dd');
        $scope.educationForm.end_date = $filter("date")(education.end_date, 'yyyy-MM-dd');
        $scope.isCollapsed = false;
    };

    $scope.updateEducation = function () {

        EducationService.update($scope.educationForm).then(function (result) {

            for (i = 0; i < $scope.educations.length; i++) {
                if ($scope.educations[i].id == $scope.educationForm.id)
                    $scope.educations[i] = $scope.educationForm;
            }

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

    $scope.deleteEducation = function (education) {
        var result = confirm("Вы действительно хотите удалить данную запись?");
        if (result) {
            EducationService.delete(education).then(function (result) {
                for (i = 0; i < $scope.educations.length; i++) {
                    if ($scope.educations[i].id == education.id)
                        $scope.educations.splice(i, 1);
                }
                PNotify.desktop.permission();
                (new PNotify({
                    title: 'Статус удаления',
                    text: 'Удаление успешно выполнен.',
                    desktop: {
                        desktop: true
                    }
                })).get().click(function (e) {
                        if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                    });
            });
        }
    }

    $scope.institutionModalOpen = function (size) {

        var modalInstance = $modal.open({
            templateUrl: 'institutionModalContent.html',
            controller: 'ModalInstitutionController',
            size: size
        });

        modalInstance.result.then(function (result) {
            $scope.institutions.push(result.data);
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

    $scope.qualificationModalOpen = function (size) {

        var modalInstance = $modal.open({
            templateUrl: 'qualificationModalContent.html',
            controller: 'ModalQualificationController',
            size: size
        });

        modalInstance.result.then(function (result) {
            $scope.qualifications.push(result.data);
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

});

angular.module('app').controller('ModalInstitutionController', function ($scope, $modalInstance, InstitutionService) {

    $scope.institutionForm = {
        id: 0,
        shortname: null,
        longname: null
    };
    $scope.saveInstitution = function () {

        $scope.ok(InstitutionService.save($scope.institutionForm));

        $scope.institutionForm = {
            id: 0,
            shortname: null,
            longname: null
        };
    }

    $scope.ok = function (result) {
        $modalInstance.close(result);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('app').controller('ModalQualificationController', function ($scope, $modalInstance, QualificationService) {

    $scope.qualificationForm = {
        id: 0,
        name: null
    };
    $scope.saveQualification = function () {

        $scope.ok(QualificationService.save($scope.qualificationForm));

        $scope.qualificationForm = {
            id: 0,
            name: null
        };
    }

    $scope.ok = function (result) {
        console.log(result);
        $modalInstance.close(result);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('app').controller('SeminarController', function ($scope, $filter, seminarsData, SeminarService) {
    $scope.seminars = seminarsData;

    $scope.isCollapsed = true;

    $scope.saveSeminar = function () {
        $scope.seminarForm.id = 0;
        $scope.seminarForm.employee_id = $scope.activeEmployee.id;

        SeminarService.save($scope.seminarForm).then(function (result) {
            $scope.seminars.push(result.data);
            $scope.resetSeminar();
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
    }

    $scope.resetSeminar = function () {
        $scope.selectAction = $scope.saveSeminar;
        $scope.isCollapsed = false;
        $scope.seminarForm = {
            id: null,
            employee_id: null,
            topic: null,
            organizer: null,
            event_date: null,
            hasCertificate: false,
            created_at: '2001-01-01T00:00:00',
            updated_at: '2001-01-01T00:00:00'
        };
    }


    $scope.resetSeminar();

    $scope.selectSeminar = function (seminar) {
        $scope.selectAction = $scope.updateSeminar;
        $scope.seminarForm = seminar;
        $scope.seminarForm.event_date = $filter("date")(seminar.event_date, 'yyyy-MM-dd');
        $scope.isCollapsed = false;
    };

    $scope.updateSeminar = function () {

        SeminarService.update($scope.seminarForm).then(function (result) {

            for (i = 0; i < $scope.seminars.length; i++) {
                if ($scope.seminars[i].id == $scope.seminarForm.id)
                    $scope.seminars[i] = $scope.seminarForm;
            }

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

    $scope.deleteSeminar = function (seminar) {
        var result = confirm("Вы действительно хотите удалить данную запись?");
        if (result) {
            SeminarService.delete(seminar).then(function (result) {
                for (i = 0; i < $scope.seminars.length; i++) {
                    if ($scope.seminars[i].id == seminar.id)
                        $scope.seminars.splice(i, 1);
                }
                PNotify.desktop.permission();
                (new PNotify({
                    title: 'Статус удаления',
                    text: 'Удаление успешно выполнен.',
                    desktop: {
                        desktop: true
                    }
                })).get().click(function (e) {
                        if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                    });
            });
        }
    }

});

