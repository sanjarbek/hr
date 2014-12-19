# --- !Ups

CREATE TABLE leaving_reasons (
  id    SERIAL PRIMARY KEY,
  punkt TEXT NOT NULL UNIQUE,
  name       TEXT      NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

INSERT INTO leaving_reasons (punkt, name, created_at, updated_at)
VALUES ('ст. 80 Трудового Кодекса КР', 'Расторжение трудового договора по соглашению сторон', localtimestamp,
        localtimestamp),
('ст. 81 Трудового Кодекса КР', 'Расторжение срочного трудового договора с истечением его срока', localtimestamp, localtimestamp),
('ст. 82 Трудового Кодекса КР', 'Расторжение трудового договора по инициативе работника (по собственному желанию) Расторжение трудового договора по инициативе работодателя', localtimestamp, localtimestamp),
('п.1 ст. 83 Трудового Кодекса КР', 'Ликвидация организации (юридического лица), прекращения деятельности работодателя (физического лица)', localtimestamp, localtimestamp),
('п.2 ст. 83 Трудового Кодекса КР', 'Сокращение численности или штата работников, в том числев связи с реорганизацией организации', localtimestamp, localtimestamp),
('п.3.а ст. 83 Трудового Кодекса КР', 'Несоответсвие работника занимаемой должности или выполняемой работе вследствие состояния здоровья в соотвествие с медицинским заключением', localtimestamp, localtimestamp),
('п.3.б ст. 83 Трудового Кодекса КР', 'Несоответсвие работника занимаемой должности или выполняемой работе вследствие недостаточной квалификации, подтвержденной результатами аттестации, справками о невыполнении норм труда, актами о выпуске брака и другими данными.', localtimestamp, localtimestamp),
('п.4 ст. 83 Трудового Кодекса КР', 'Смена собственника имущества организации (в отношении руководителя организации, его заместителей и главного бухгалтера)', localtimestamp, localtimestamp),
('п.5 ст. 83 Трудового Кодекса КР', 'Неоднократное неисполнение работником без уважительных причин трудовых обязанностей, если он имеет дисциплинарное взыскание', localtimestamp, localtimestamp),
('п.6.а ст. 83 Трудового Кодекса КР', 'Однократное грубое нарушение работником трудовых обязанностей - прогул (отсуствие на работе более 3 часов подряд в течение рабочего дня без уважительных причин)', localtimestamp, localtimestamp),
('п.6.б ст. 83 Трудового Кодекса КР', 'Однократное грубое нарушение работником трудовых обязанностей - появление на работе в состоянии алкогольного, наркотического или токсического опьянения. Такое состояние подтверждается медицинским заключением, свидетельскими показаниями или актом, составленным работодателем совместно с представительным органом работников.', localtimestamp, localtimestamp),
('п.6.в ст. 83 Трудового Кодекса КР', 'Однократное грубое нарушение работником трудовых обязанностей - совершение по месту работы умышленной порчи или хищения имущества организации', localtimestamp, localtimestamp),
('п.6.г ст. 83 Трудового Кодекса КР', 'Однократное грубое нарушение работником трудовых обязанностей - нарушение работником требований охраны труда, которое повлекло тяжкие последствия, включая травмы, авария', localtimestamp, localtimestamp),
('п.6.д ст. 83 Трудового Кодекса КР', 'Однократное грубое нарушение работником трудовых обязанностей - разглашение государственной, служебной, коммерческой или иной охраняемой законом тайны, ставшей известной работнику в связи с исполнением трудовых обязанностей и если условие о ее сохранении предусмотрено в трудовом договоре.', localtimestamp, localtimestamp),
('п.7 ст. 83 Трудового Кодекса КР', 'Совершение виновных действий работником, непосредственно обслуживающим денежные или товарные ценности, если эти действия дают основание для утраты доверия к нему со стороны работодателя', localtimestamp, localtimestamp),
('п.8 ст. 83 Трудового Кодекса КР', 'Совершение работником, выполняющим воспитательные функции, аморального поступка, не совместимого с продолжением данной работы', localtimestamp, localtimestamp),
('п.9 ст. 83 Трудового Кодекса КР', 'Принятие необоснованного решения руководителем организации (филиала, представительства), его заместителями и главным бухгалтером, повлекшего за собой нарушение сохранности, неправомерное использование или иной ущерб имуществу организации', localtimestamp, localtimestamp),
('п.10 ст. 83 Трудового Кодекса КР', 'Представление работником работодателю подложных документов или заведомо ложных сведений при заключении трудового договора, если эти документы или сведения могли являться основанием для отказа в заключении трудового договора', localtimestamp, localtimestamp),
('п.11 ст. 83 Трудового Кодекса КР', 'Однократное грубое нарушение руководителем организации (филиала, представительства), его заместителями своих трудовых обязанностей', localtimestamp, localtimestamp),
('п.12 ст. 83 Трудового Кодекса КР', 'В других случаях, установленных настоящим Кодексом или иными законами', localtimestamp, localtimestamp),
('ст. 84 Трудового Кодекса КР', 'Расторжение трудового договора по инициативе работодателя с предварительного согласия представительного органа работников Прекращение трудового договора по обстоятельствам, не зависящим от воли сторон', localtimestamp, localtimestamp),
('п.1 ст. 88 Трудового Кодекса КР', 'Призыв работника на военную или заменяющую ее альтернативную службу, а также в связи с переводом супруга (супруги) на службу в другую местность', localtimestamp, localtimestamp),
('п.2 ст. 88 Трудового Кодекса КР', 'Восстановление работника, который ранее выполнял эту работу, по решению государственной инспекции труда или суда', localtimestamp, localtimestamp),
('п.3 ст. 88 Трудового Кодекса КР', 'Нарушение установленных правил приема на работу', localtimestamp, localtimestamp),
('п.4 ст. 88 Трудового Кодекса КР', 'Осуждение работника к наказанию, исключающему продолжение прежней работы, в соотвествии с приговором суда, вступившим в законную силу', localtimestamp, localtimestamp),
('п.5 ст. 88 Трудового Кодекса КР', 'Неизбрание на выборную должность', localtimestamp, localtimestamp),
('п.6 ст. 88 Трудового Кодекса КР', 'Смерть работника либо работодателя - физического лица, а также признание судом работника или работодателя - физического лица умершим или безвестно отсуствующим', localtimestamp, localtimestamp),
('п.7 ст. 88 Трудового Кодекса КР', 'Наступление чрезвычайных обстоятельств, препятствующих продолжению трудовых отношений (военные действия, катастрофа, стихийное бедствие и другие чрезвычайные обстоятельства), если данное обстоятельство признано решением Правительства КР. Прочие основания прекращения (расторжения) трудового договора', localtimestamp, localtimestamp),
('ч.3 ст. 75 Трудового Кодекса КР', 'Отказ работника от перевода на другую работу вследствие состояния здоровья в соответствии с медицинским заключением', localtimestamp, localtimestamp),
('ч.4 ст. 71 Трудового Кодекса КР', 'Отказ работника от продолжения работы в связи с изменением существенных условий труда', localtimestamp, localtimestamp),
('ст. 77 Трудового Кодекса КР', 'Отказ работника от продолжения работы в организации в связи со сменой собственника, изменением ее подчиненности (подведомственности) и ее реорганизации', localtimestamp, localtimestamp),
  ('ч.1 ст. 69 Трудового Кодекса КР',
   'Отказ работника от перевода в связи с перемещением работодателя в другую местность', localtimestamp,
   localtimestamp);


CREATE TABLE order_types (
  id         SERIAL PRIMARY KEY,
  name       TEXT      NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

INSERT INTO order_types (name, created_at, updated_at) VALUES
  ('Прием на работу', localtimestamp, localtimestamp),
  ('Перемещение', localtimestamp, localtimestamp),
  ('Увольнение', localtimestamp, localtimestamp);

CREATE TABLE orders (
  id             BIGSERIAL PRIMARY KEY,
  order_type_id INT REFERENCES order_types (id),
  date_of_order  DATE,
  created_at    TIMESTAMP NOT NULL,
  updated_at    TIMESTAMP NOT NULL
);

CREATE TABLE employment_orders (
  position_id        INT,
  contract_type_id   INT,
  contract_number INT,
  employee_id        BIGINT,
  salary             NUMERIC(14, 2),
  calendar_type_id   SMALLINT,
  trial_period_start DATE,
  trial_period_end   DATE,
  start_date         DATE,
  end_date           DATE,
  close_date      DATE
)
  INHERITS (orders);

CREATE TABLE dismissal_orders (
  employee_id       BIGINT    NOT NULL,
  leaving_reason_id INT       NOT NULL,
  comment           TEXT,
  leaving_date DATE
)
  INHERITS (orders);

CREATE TABLE positions (
  id                  BIGSERIAL PRIMARY KEY,
  employment_order_id BIGINT,
  position_id         INT,
  employee_id         BIGINT,
  dismissal_order_id  BIGINT,
  start_date          DATE,
  end_date            DATE,
  close_date          DATE,
  created_at          TIMESTAMP NOT NULL,
  updated_at          TIMESTAMP NOT NULL
);

--------------------------------------------------------------------------------------
-- Обновление инфы сотрудника о принятие его на работу.
CREATE OR REPLACE FUNCTION set_employee_employment_order()
  RETURNS TRIGGER AS
  $body$
  BEGIN
    UPDATE employees
    SET employment_order_id = NEW.id
    WHERE employees.id = NEW.employee_id;;
  RETURN NEW;
  ;
  END;
  ;
  $body$ LANGUAGE plpgsql;

CREATE TRIGGER new_employment_order AFTER INSERT ON employment_orders
FOR EACH ROW
EXECUTE PROCEDURE set_employee_employment_order();
--------------------------------------------------------------------------------------

--------------------------------------------------------------------------------------
-- Обновление инфы сотрудника об увольнение его с работы.
CREATE OR REPLACE FUNCTION set_employee_dismissal_order()
  RETURNS TRIGGER AS
  $body$
  BEGIN
    UPDATE employees
    SET employment_order_id = NULL
    WHERE employees.id = NEW.employee_id;;
  RETURN NEW;
  ;
  END;
  ;
  $body$ LANGUAGE plpgsql;

CREATE TRIGGER new_dismissal_order AFTER INSERT ON dismissal_orders
FOR EACH ROW
EXECUTE PROCEDURE set_employee_dismissal_order();
--------------------------------------------------------------------------------------

  # -- !Downs
DROP TABLE IF EXISTS leaving_reasons CASCADE;
DROP SEQUENCE IF EXISTS leaving_reasons_id_seq;
DROP TABLE IF EXISTS positions CASCADE;
DROP SEQUENCE IF EXISTS positions_id_seq;
DROP TABLE IF EXISTS orders CASCADE;
DROP SEQUENCE IF EXISTS orders_id_seq;
DROP TABLE IF EXISTS order_types CASCADE;
DROP SEQUENCE IF EXISTS order_types_id_seq;
DROP TABLE IF EXISTS employment_orders CASCADE;
DROP SEQUENCE IF EXISTS employment_orders_id_seq;
DROP TABLE IF EXISTS dismissal_orders CASCADE;
DROP SEQUENCE IF EXISTS dismissal_orders_id_seq;

