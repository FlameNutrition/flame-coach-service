-- MariaDB dump 10.19  Distrib 10.5.9-MariaDB, for osx10.16 (x86_64)
--
-- Host: localhost    Database: flame-coach
-- ------------------------------------------------------

--
-- Table structure for table `Client_Measure_Weight_Seq`
--

CREATE TABLE `Client_Measure_Weight_Seq`
(
    `next_val` bigint DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `hibernate_sequence`
--

CREATE TABLE `hibernate_sequence`
(
    `next_val` bigint DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `Client_Type`
--

CREATE TABLE `Client_Type`
(
    `id`   bigint       NOT NULL,
    `type` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `User_Session`
--

CREATE TABLE `User_Session`
(
    `id`             bigint       NOT NULL,
    `expirationDate` datetime(6)  NOT NULL,
    `token`          varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_jiu6slb2w59f3f6w96qorudn7` (`token`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `User`
--

CREATE TABLE `User`
(
    `id`            bigint       NOT NULL,
    `email`         varchar(255) NOT NULL,
    `password`      varchar(255) NOT NULL,
    `userSessionFk` bigint       NOT NULL,
    `keyDecrypt`    varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_mar7bx6urx3sjlhjt104j61ib` (`userSessionFk`),
    UNIQUE KEY `UK_e6gkqunxajvyxl5uctpl2vl2p` (`email`),
    CONSTRAINT `FKikpgpgltl8bnnasw0lujorr4q` FOREIGN KEY (`userSessionFk`) REFERENCES `User_Session` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- Table structure for table `Country_Config`
--

CREATE TABLE `Country_Config`
(
    `id`            bigint       NOT NULL,
    `countryCode`   varchar(255) NOT NULL,
    `externalValue` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `Gender_Config`
--

CREATE TABLE `Gender_Config`
(
    `id`            bigint       NOT NULL,
    `externalValue` varchar(255) NOT NULL,
    `genderCode`    varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `Coach`
--

CREATE TABLE `Coach`
(
    `id`               bigint       NOT NULL,
    `birthday`         date         DEFAULT NULL,
    `firstName`        varchar(255) NOT NULL,
    `lastName`         varchar(255) NOT NULL,
    `phoneCode`        varchar(255) DEFAULT NULL,
    `phoneNumber`      varchar(255) DEFAULT NULL,
    `uuid`             varchar(255) NOT NULL,
    `clientTypeFk`     bigint       NOT NULL,
    `countryFk`        bigint       DEFAULT NULL,
    `genderFk`         bigint       DEFAULT NULL,
    `userFk`           bigint       NOT NULL,
    `registrationDate` date         NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_7yt4upu956oo2vtot6rx0i3fi` (`userFk`),
    UNIQUE KEY `UK_cqnacss4k9c6ryx4lvai8x83r` (`uuid`),
    KEY `FKrqlua0mfl3cg89e2ai9c2thad` (`clientTypeFk`),
    KEY `FKch8f3plbukvcarxit4et1udl8` (`countryFk`),
    KEY `FKako7umopx34g08lkc8bj4thm7` (`genderFk`),
    CONSTRAINT `FKako7umopx34g08lkc8bj4thm7` FOREIGN KEY (`genderFk`) REFERENCES `Gender_Config` (`id`),
    CONSTRAINT `FKch8f3plbukvcarxit4et1udl8` FOREIGN KEY (`countryFk`) REFERENCES `Country_Config` (`id`),
    CONSTRAINT `FKr8vw5krmwc7lucfwxg4pgxs9v` FOREIGN KEY (`userFk`) REFERENCES `User` (`id`),
    CONSTRAINT `FKrqlua0mfl3cg89e2ai9c2thad` FOREIGN KEY (`clientTypeFk`) REFERENCES `Client_Type` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--

--
-- Table structure for table `Client`
--

CREATE TABLE `Client`
(
    `id`               bigint       NOT NULL,
    `birthday`         date                  DEFAULT NULL,
    `firstName`        varchar(255) NOT NULL,
    `lastName`         varchar(255) NOT NULL,
    `phoneCode`        varchar(255)          DEFAULT NULL,
    `phoneNumber`      varchar(255)          DEFAULT NULL,
    `uuid`             varchar(255) NOT NULL,
    `clientTypeFk`     bigint       NOT NULL,
    `coachFk`          bigint                DEFAULT NULL,
    `countryFk`        bigint                DEFAULT NULL,
    `genderFk`         bigint                DEFAULT NULL,
    `userFk`           bigint       NOT NULL,
    `clientStatus`     varchar(255) NOT NULL,
    `registrationDate` date         NOT NULL,
    `height`           float                 DEFAULT '0',
    `weight`           float                 DEFAULT '0',
    `measureConfig`    varchar(100) NOT NULL DEFAULT 'KG_CM',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_j5r3rlyrexqkn2xktfjyntj45` (`userFk`),
    UNIQUE KEY `UK_kdqusfe5qtmk4dpaf1meyjkok` (`uuid`),
    KEY `FK8a44e4yc8omgym1bt1npit7t9` (`clientTypeFk`),
    KEY `FK35cub2umokijhxumo6hsxh1a7` (`coachFk`),
    KEY `FKg9sh4d6eynxf5xd8ee8lju18k` (`countryFk`),
    KEY `FK75jl4simjlk0ip32wcmgpr26t` (`genderFk`),
    CONSTRAINT `FK35cub2umokijhxumo6hsxh1a7` FOREIGN KEY (`coachFk`) REFERENCES `Coach` (`id`),
    CONSTRAINT `FK75jl4simjlk0ip32wcmgpr26t` FOREIGN KEY (`genderFk`) REFERENCES `Gender_Config` (`id`),
    CONSTRAINT `FK8a44e4yc8omgym1bt1npit7t9` FOREIGN KEY (`clientTypeFk`) REFERENCES `Client_Type` (`id`),
    CONSTRAINT `FKg9sh4d6eynxf5xd8ee8lju18k` FOREIGN KEY (`countryFk`) REFERENCES `Country_Config` (`id`),
    CONSTRAINT `FKt73y6cea56y7or5hig943pfdl` FOREIGN KEY (`userFk`) REFERENCES `User` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `Client_Measure_Weight`
--

CREATE TABLE `Client_Measure_Weight`
(
    `id`          bigint NOT NULL,
    `measureDate` date   NOT NULL,
    `weight`      float  NOT NULL,
    `clientFk`    bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKno3mu57pv25w0mqmhpa4swo8u` (`clientFk`),
    CONSTRAINT `FKno3mu57pv25w0mqmhpa4swo8u` FOREIGN KEY (`clientFk`) REFERENCES `Client` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `Daily_Task`
--

CREATE TABLE `Daily_Task`
(
    `id`          bigint       NOT NULL,
    `date`        date         NOT NULL,
    `description` varchar(255) NOT NULL,
    `name`        varchar(255) NOT NULL,
    `ticked`      bit(1)       NOT NULL,
    `uuid`        varchar(255) NOT NULL,
    `clientFk`    bigint       NOT NULL,
    `createdByFk` bigint       NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_6jbbjh0dt8dwtgx63rv90otxg` (`uuid`),
    KEY `FKdsu2shjre4aafj3qk3r3l8b7o` (`clientFk`),
    KEY `FK78lyb46m9s6c7pf8s3kbvq6hg` (`createdByFk`),
    CONSTRAINT `FK78lyb46m9s6c7pf8s3kbvq6hg` FOREIGN KEY (`createdByFk`) REFERENCES `Coach` (`id`),
    CONSTRAINT `FKdsu2shjre4aafj3qk3r3l8b7o` FOREIGN KEY (`clientFk`) REFERENCES `Client` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
INSERT INTO `hibernate_sequence`
VALUES (364);
UNLOCK TABLES;

--
-- Dumping data for table `Client_Measure_Weight_Seq`
--

LOCK TABLES `Client_Measure_Weight_Seq` WRITE;
INSERT INTO `Client_Measure_Weight_Seq`
VALUES (40);
UNLOCK TABLES;

--
-- Dumping data for table `Client_Type`
--

LOCK TABLES `Client_Type` WRITE;
INSERT INTO `Client_Type`
VALUES (1, 'COACH'),
       (2, 'CLIENT');
UNLOCK TABLES;

--
-- Dumping data for table `Gender_Config`
--

LOCK TABLES `Gender_Config` WRITE;
INSERT INTO `Gender_Config`
VALUES (1, 'Female', 'F'),
       (2, 'Male', 'M');
UNLOCK TABLES;

--
-- Dumping data for table `Country_Config`
--

LOCK TABLES `Country_Config` WRITE;
INSERT INTO `Country_Config`
VALUES (1, 'AF', 'Afghanistan'),
       (2, 'AL', 'Albania'),
       (3, 'DZ', 'Algeria'),
       (4, 'AS', 'American Samoa'),
       (5, 'AD', 'Andorra'),
       (6, 'AO', 'Angola'),
       (7, 'AI', 'Anguilla'),
       (8, 'AQ', 'Antarctica'),
       (9, 'AG', 'Antigua and Barbuda'),
       (10, 'AR', 'Argentina'),
       (11, 'AM', 'Armenia'),
       (12, 'AW', 'Aruba'),
       (13, 'AU', 'Australia'),
       (14, 'AT', 'Austria'),
       (15, 'AZ', 'Azerbaijan'),
       (16, 'BS', 'Bahamas (the)'),
       (17, 'BH', 'Bahrain'),
       (18, 'BD', 'Bangladesh'),
       (19, 'BB', 'Barbados'),
       (20, 'BY', 'Belarus'),
       (21, 'BE', 'Belgium'),
       (22, 'BZ', 'Belize'),
       (23, 'BJ', 'Benin'),
       (24, 'BM', 'Bermuda'),
       (25, 'BT', 'Bhutan'),
       (26, 'BO', 'Bolivia (Plurinational State of)'),
       (27, 'BQ', 'Bonaire, Sint Eustatius and Saba'),
       (28, 'BA', 'Bosnia and Herzegovina'),
       (29, 'BW', 'Botswana'),
       (30, 'BV', 'Bouvet Island'),
       (31, 'BR', 'Brazil'),
       (32, 'IO', 'British Indian Ocean Territory (the)'),
       (33, 'BN', 'Brunei Darussalam'),
       (34, 'BG', 'Bulgaria'),
       (35, 'BF', 'Burkina Faso'),
       (36, 'BI', 'Burundi'),
       (37, 'CV', 'Cabo Verde'),
       (38, 'KH', 'Cambodia'),
       (39, 'CM', 'Cameroon'),
       (40, 'CA', 'Canada'),
       (41, 'KY', 'Cayman Islands (the)'),
       (42, 'CF', 'Central African Republic (the)'),
       (43, 'TD', 'Chad'),
       (44, 'CL', 'Chile'),
       (45, 'CN', 'China'),
       (46, 'CX', 'Christmas Island'),
       (47, 'CC', 'Cocos (Keeling) Islands (the)'),
       (48, 'CO', 'Colombia'),
       (49, 'KM', 'Comoros (the)'),
       (50, 'CD', 'Congo (the Democratic Republic of the)'),
       (51, 'CG', 'Congo (the)'),
       (52, 'CK', 'Cook Islands (the)'),
       (53, 'CR', 'Costa Rica'),
       (54, 'HR', 'Croatia'),
       (55, 'CU', 'Cuba'),
       (56, 'CW', 'Curaçao'),
       (57, 'CY', 'Cyprus'),
       (58, 'CZ', 'Czechia'),
       (59, 'CI', 'Côte d\'Ivoire'),
       (60, 'DK', 'Denmark'),
       (61, 'DJ', 'Djibouti'),
       (62, 'DM', 'Dominica'),
       (63, 'DO', 'Dominican Republic (the)'),
       (64, 'EC', 'Ecuador'),
       (65, 'EG', 'Egypt'),
       (66, 'SV', 'El Salvador'),
       (67, 'GQ', 'Equatorial Guinea'),
       (68, 'ER', 'Eritrea'),
       (69, 'EE', 'Estonia'),
       (70, 'SZ', 'Eswatini'),
       (71, 'ET', 'Ethiopia'),
       (72, 'FK', 'Falkland Islands (the) [Malvinas]'),
       (73, 'FO', 'Faroe Islands (the)'),
       (74, 'FJ', 'Fiji'),
       (75, 'FI', 'Finland'),
       (76, 'FR', 'France'),
       (77, 'GF', 'French Guiana'),
       (78, 'PF', 'French Polynesia'),
       (79, 'TF', 'French Southern Territories (the)'),
       (80, 'GA', 'Gabon'),
       (81, 'GM', 'Gambia (the)'),
       (82, 'GE', 'Georgia'),
       (83, 'DE', 'Germany'),
       (84, 'GH', 'Ghana'),
       (85, 'GI', 'Gibraltar'),
       (86, 'GR', 'Greece'),
       (87, 'GL', 'Greenland'),
       (88, 'GD', 'Grenada'),
       (89, 'GP', 'Guadeloupe'),
       (90, 'GU', 'Guam'),
       (91, 'GT', 'Guatemala'),
       (92, 'GG', 'Guernsey'),
       (93, 'GN', 'Guinea'),
       (94, 'GW', 'Guinea-Bissau'),
       (95, 'GY', 'Guyana'),
       (96, 'HT', 'Haiti'),
       (97, 'HM', 'Heard Island and McDonald Islands'),
       (98, 'VA', 'Holy See (the)'),
       (99, 'HN', 'Honduras'),
       (100, 'HK', 'Hong Kong'),
       (101, 'HU', 'Hungary'),
       (102, 'IS', 'Iceland'),
       (103, 'IN', 'India'),
       (104, 'ID', 'Indonesia'),
       (105, 'IR', 'Iran (Islamic Republic of)'),
       (106, 'IQ', 'Iraq'),
       (107, 'IE', 'Ireland'),
       (108, 'IM', 'Isle of Man'),
       (109, 'IL', 'Israel'),
       (110, 'IT', 'Italy'),
       (111, 'JM', 'Jamaica'),
       (112, 'JP', 'Japan'),
       (113, 'JE', 'Jersey'),
       (114, 'JO', 'Jordan'),
       (115, 'KZ', 'Kazakhstan'),
       (116, 'KE', 'Kenya'),
       (117, 'KI', 'Kiribati'),
       (118, 'KP', 'Korea (the Democratic People\'s Republic of)'),
       (119, 'KR', 'Korea (the Republic of)'),
       (120, 'KW', 'Kuwait'),
       (121, 'KG', 'Kyrgyzstan'),
       (122, 'LA', 'Lao People\'s Democratic Republic (the)'),
       (123, 'LV', 'Latvia'),
       (124, 'LB', 'Lebanon'),
       (125, 'LS', 'Lesotho'),
       (126, 'LR', 'Liberia'),
       (127, 'LY', 'Libya'),
       (128, 'LI', 'Liechtenstein'),
       (129, 'LT', 'Lithuania'),
       (130, 'LU', 'Luxembourg'),
       (131, 'MO', 'Macao'),
       (132, 'MG', 'Madagascar'),
       (133, 'MW', 'Malawi'),
       (134, 'MY', 'Malaysia'),
       (135, 'MV', 'Maldives'),
       (136, 'ML', 'Mali'),
       (137, 'MT', 'Malta'),
       (138, 'MH', 'Marshall Islands (the)'),
       (139, 'MQ', 'Martinique'),
       (140, 'MR', 'Mauritania'),
       (141, 'MU', 'Mauritius'),
       (142, 'YT', 'Mayotte'),
       (143, 'MX', 'Mexico'),
       (144, 'FM', 'Micronesia (Federated States of)'),
       (145, 'MD', 'Moldova (the Republic of)'),
       (146, 'MC', 'Monaco'),
       (147, 'MN', 'Mongolia'),
       (148, 'ME', 'Montenegro'),
       (149, 'MS', 'Montserrat'),
       (150, 'MA', 'Morocco'),
       (151, 'MZ', 'Mozambique'),
       (152, 'MM', 'Myanmar'),
       (153, 'NA', 'Namibia'),
       (154, 'NR', 'Nauru'),
       (155, 'NP', 'Nepal'),
       (156, 'NL', 'Netherlands (the)'),
       (157, 'NC', 'New Caledonia'),
       (158, 'NZ', 'New Zealand'),
       (159, 'NI', 'Nicaragua'),
       (160, 'NE', 'Niger (the)'),
       (161, 'NG', 'Nigeria'),
       (162, 'NU', 'Niue'),
       (163, 'NF', 'Norfolk Island'),
       (164, 'MP', 'Northern Mariana Islands (the)'),
       (165, 'NO', 'Norway'),
       (166, 'OM', 'Oman'),
       (167, 'PK', 'Pakistan'),
       (168, 'PW', 'Palau'),
       (169, 'PS', 'Palestine, State of'),
       (170, 'PA', 'Panama'),
       (171, 'PG', 'Papua New Guinea'),
       (172, 'PY', 'Paraguay'),
       (173, 'PE', 'Peru'),
       (174, 'PH', 'Philippines (the)'),
       (175, 'PN', 'Pitcairn'),
       (176, 'PL', 'Poland'),
       (177, 'PT', 'Portugal'),
       (178, 'PR', 'Puerto Rico'),
       (179, 'QA', 'Qatar'),
       (180, 'MK', 'Republic of North Macedonia'),
       (181, 'RO', 'Romania'),
       (182, 'RU', 'Russian Federation (the)'),
       (183, 'RW', 'Rwanda'),
       (184, 'RE', 'Réunion'),
       (185, 'BL', 'Saint Barthélemy'),
       (186, 'SH', 'Saint Helena, Ascension and Tristan da Cunha'),
       (187, 'KN', 'Saint Kitts and Nevis'),
       (188, 'LC', 'Saint Lucia'),
       (189, 'MF', 'Saint Martin (French part)'),
       (190, 'PM', 'Saint Pierre and Miquelon'),
       (191, 'VC', 'Saint Vincent and the Grenadines'),
       (192, 'WS', 'Samoa'),
       (193, 'SM', 'San Marino'),
       (194, 'ST', 'Sao Tome and Principe'),
       (195, 'SA', 'Saudi Arabia'),
       (196, 'SN', 'Senegal'),
       (197, 'RS', 'Serbia'),
       (198, 'SC', 'Seychelles'),
       (199, 'SL', 'Sierra Leone'),
       (200, 'SG', 'Singapore'),
       (201, 'SX', 'Sint Maarten (Dutch part)'),
       (202, 'SK', 'Slovakia'),
       (203, 'SI', 'Slovenia'),
       (204, 'SB', 'Solomon Islands'),
       (205, 'SO', 'Somalia'),
       (206, 'ZA', 'South Africa'),
       (207, 'GS', 'South Georgia and the South Sandwich Islands'),
       (208, 'SS', 'South Sudan'),
       (209, 'ES', 'Spain'),
       (210, 'LK', 'Sri Lanka'),
       (211, 'SD', 'Sudan (the)'),
       (212, 'SR', 'Suriname'),
       (213, 'SJ', 'Svalbard and Jan Mayen'),
       (214, 'SE', 'Sweden'),
       (215, 'CH', 'Switzerland'),
       (216, 'SY', 'Syrian Arab Republic'),
       (217, 'TW', 'Taiwan'),
       (218, 'TJ', 'Tajikistan'),
       (219, 'TZ', 'Tanzania, United Republic of'),
       (220, 'TH', 'Thailand'),
       (221, 'TL', 'Timor-Leste'),
       (222, 'TG', 'Togo'),
       (223, 'TK', 'Tokelau'),
       (224, 'TO', 'Tonga'),
       (225, 'TT', 'Trinidad and Tobago'),
       (226, 'TN', 'Tunisia'),
       (227, 'TR', 'Turkey'),
       (228, 'TM', 'Turkmenistan'),
       (229, 'TC', 'Turks and Caicos Islands (the)'),
       (230, 'TV', 'Tuvalu'),
       (231, 'UG', 'Uganda'),
       (232, 'UA', 'Ukraine'),
       (233, 'AE', 'United Arab Emirates (the)'),
       (234, 'GB', 'United Kingdom of Great Britain and Northern Ireland (the)'),
       (235, 'UM', 'United States Minor Outlying Islands (the)'),
       (236, 'US', 'United States of America (the)'),
       (237, 'UY', 'Uruguay'),
       (238, 'UZ', 'Uzbekistan'),
       (239, 'VU', 'Vanuatu'),
       (240, 'VE', 'Venezuela (Bolivarian Republic of)'),
       (241, 'VN', 'Viet Nam'),
       (242, 'VG', 'Virgin Islands (British)'),
       (243, 'VI', 'Virgin Islands (U.S.)'),
       (244, 'WF', 'Wallis and Futuna'),
       (245, 'EH', 'Western Sahara'),
       (246, 'YE', 'Yemen'),
       (247, 'ZM', 'Zambia'),
       (248, 'ZW', 'Zimbabwe'),
       (249, 'AX', 'Åland Islands');
UNLOCK TABLES;

--
-- Dumping data for table `User_Session`
--

LOCK TABLES `User_Session` WRITE;
INSERT INTO `User_Session`
VALUES (3, '2021-06-01 16:03:40.957417', '0cdfe10c-b0c9-4525-bd86-7c3f5f53739e'),
       (6, '2021-04-28 17:37:40.217527', 'e23fa4ff-4c68-408d-bef6-ac414c6ba99b'),
       (9, '2021-04-15 14:39:50.571901', '3f0cbe72-86d0-4f91-9644-020698636a02'),
       (12, '2021-04-08 09:03:38.501954', '6b887243-655b-4a1f-aaad-ae2bbc42aad5'),
       (15, '2021-03-14 08:57:55.655240', '9627f05a-fe71-4d00-805b-5ef002c9c4ce'),
       (18, '2021-04-14 16:25:10.049623', '28944ad3-95c6-4ed2-8e6a-39b1d1b4e21d'),
       (21, '2021-03-14 08:58:24.702708', '3ccedb66-3fc0-4dcc-b8d9-ce46a4d26f67'),
       (24, '2021-03-14 08:58:35.803434', 'c2f5aec3-7b3e-4392-8555-b335d23075a3'),
       (27, '2021-04-15 14:40:47.954019', '43fdad81-00c2-4cd0-817e-29ca74a37314'),
       (30, '2021-06-01 15:31:48.550292', '0a229369-4d13-4ec7-93ba-5be542f642b9'),
       (33, '2021-04-15 10:05:05.496248', 'b25280a7-5ecf-4480-8d59-5bf4eb3a59b0'),
       (36, '2021-03-20 09:11:31.020097', '04da4522-a823-44f9-a20c-e2ef74216657'),
       (39, '2021-03-20 09:11:46.692348', 'b075afb2-1153-4b09-9032-545818838b54'),
       (113, '2021-03-30 12:04:59.014538', 'eb1be981-ddb6-4fea-97b8-698815ca0ce2'),
       (150, '2021-04-08 10:02:06.350269', 'ffbb4273-53ec-4487-8638-1932adaf307b'),
       (242, '2021-04-13 18:14:56.915871', '6365859a-ef01-41b4-873b-b2789ed36f5d'),
       (249, '2021-05-29 10:41:36.772889', '844ffc95-b6d6-4043-9dfb-2a54f1f06ff9'),
       (255, '2021-04-18 10:34:15.832150', '9aeb1c6d-d81f-4a17-9c2e-8c93a00923c7'),
       (298, '2021-05-12 15:47:53.086867', '92427d20-d7b8-4105-ba9d-48b94609a753'),
       (301, '2021-05-29 10:38:13.986072', '69b07fbd-0e75-416c-ae36-20469304b9f0');
UNLOCK TABLES;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
INSERT INTO `User`
VALUES (2, 'nbento.neves@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 3,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (5, 'admin@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 6,
        '8fuwyfr4Bi2TRGifnExp'),
       (8, 'client1@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 9,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (11, 'client2@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 12,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (14, 'client3@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 15,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (17, 'client4@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 18,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (20, 'client5@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 21,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (23, 'client6@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 24,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (26, 'client7@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 27,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (29, 'nesmesquita@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 30,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (32, 'test1@test.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 33,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (35, 'c1@gmail.com', 'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==',
        36, 'Q+ZVJ/VnHINqvaY3xzrL'),
       (38, 'qeqw@qweqwe.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 39,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (112, 'client10@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 113,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (149, 'test@test3334.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 150,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (241, 'nbento.neves@gmsdasdasdail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 242,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (248, 'nbento.neves1@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 249,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (254, 'test1000@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 255,
        'Q+ZVJ/VnHINqvaY3xzrL'),
       (297, 'zuki@gmail.com',
        'GZy6aLGzi93+2Si6j5dDDH0nUPVJlL6L4JcLTVr7BaiMwTxKhZdqPfPbYwm0yXsotzQCrWbfDpVxyhHbYoboog==', 298,
        'BxFwHxShPkixgAdClZTr'),
       (300, 'eutest@gmail.com',
        'kts/PM3lo5kg9cpyN6Wa2rsOkado5tDslkdnA+V88V0AJLDpw8qzUV6B0XgCneDFYrpLntaNCYSTQbO6HtpwZw==', 301,
        'RN1S+umlHMbqORjG2+KB');
UNLOCK TABLES;

--
-- Dumping data for table `Coach`
--

LOCK TABLES `Coach` WRITE;
INSERT INTO `Coach`
VALUES (4, NULL, 'Nuno', 'Neves', '', '22323', 'ef543d94-f0db-4e17-8fa3-16e2607b4c6c', 1, NULL, NULL, 5, '2021-03-14'),
       (28, NULL, 'Ines', 'Mesquita', NULL, NULL, '5ec75ea3-3bbe-486b-8092-0ce793f6170f', 1, NULL, NULL, 29,
        '2021-03-19');
UNLOCK TABLES;

--
-- Dumping data for table `Client`
--

LOCK TABLES `Client` WRITE;
INSERT INTO `Client`
VALUES (1, NULL, 'Nuno', 'Bento', '', '232323', '79275cc8-ed8a-4f8a-b790-ff66f74d758a', 2, 28, 234, 2, 2, 'ACCEPTED',
        '2021-03-14', 0, 10.5, 'KG_CM'),
       (7, NULL, 'Bruno', 'Teles', NULL, NULL, '9f429065-166f-48fb-8e52-aca54d0661c3', 2, 4, NULL, NULL, 8, 'PENDING',
        '2021-03-13', 0, 0, 'KG_CM'),
       (10, NULL, 'Joana', 'Pinto', NULL, NULL, '86a55c96-2e66-4410-8810-d6ebc03fddaf', 2, NULL, NULL, NULL, 11,
        'AVAILABLE', '2021-03-12', 0, 0, 'KG_CM'),
       (13, NULL, 'Miguel', 'Rodrigues', NULL, NULL, '948e45ac-0e92-45a0-bc3c-a950792731c9', 2, NULL, NULL, NULL, 14,
        'AVAILABLE', '2021-03-11', 0, 0, 'KG_CM'),
       (16, NULL, 'Catia', 'Figos', NULL, NULL, 'ceb03404-98bb-4ffb-b796-2c6ad931fd27', 2, NULL, NULL, NULL, 17,
        'AVAILABLE', '2021-03-10', 0, 0, 'KG_CM'),
       (19, NULL, 'Ana', 'Bento', NULL, NULL, '892c99bd-8ab3-4515-bfdd-ce530e56f8ef', 2, 4, NULL, NULL, 20, 'PENDING',
        '2021-03-14', 0, 0, 'KG_CM'),
       (22, NULL, 'Luis', 'Petro', NULL, NULL, '34d0edc5-4ec8-43f8-ac9c-1d132a9a78fa', 2, NULL, NULL, NULL, 23,
        'AVAILABLE', '2021-03-14', 0, 0, 'KG_CM'),
       (25, NULL, 'Hugo', 'Rodriguessss', NULL, NULL, '8bfe5844-9295-4d15-a91c-3937dffc661d', 2, 4, NULL, 2, 26,
        'ACCEPTED', '2021-03-14', 0, 0, 'KG_CM'),
       (31, NULL, 'Carolina', 'Fernandes', NULL, NULL, 'c4fad329-31a0-45ba-9d34-9413ff57964b', 2, NULL, NULL, NULL, 32,
        'AVAILABLE', '2021-03-20', 0, 0, 'KG_CM'),
       (34, NULL, 'C2', 'C2', NULL, NULL, 'c838d21a-0338-4854-95ca-98fb0526a133', 2, 4, NULL, NULL, 35, 'PENDING',
        '2021-03-20', 0, 0, 'KG_CM'),
       (37, NULL, 'C4', 'C5', NULL, NULL, '9f170978-5c0c-4cf3-a15e-09fdfde9682e', 2, 4, NULL, NULL, 38, 'PENDING',
        '2021-03-20', 0, 0, 'KG_CM'),
       (111, NULL, 'Hugo', 'Rodrigues', NULL, NULL, '82fe6a11-5a75-452c-a1d4-1a509824ea5d', 2, 4, NULL, NULL, 112,
        'PENDING', '2021-03-30', 0, 0, 'KG_CM'),
       (148, NULL, 'Manuel', 'Teixeira', NULL, NULL, '1b30876e-7e55-4eac-86f7-afb0870573d2', 2, 4, NULL, 2, 149,
        'ACCEPTED', '2021-04-08', 20.5, 30.4, 'KG_CM'),
       (240, NULL, 'Nuno', 'Neves', NULL, NULL, 'e4d3cf70-974c-4d0e-9668-b30c4ca9e7b1', 2, NULL, NULL, NULL, 241,
        'AVAILABLE', '2021-04-13', 0, 0, 'KG_CM'),
       (247, NULL, 'Nuno', 'Bento', NULL, NULL, '1107978a-936b-4eb3-b929-6fee88bef533', 2, NULL, NULL, NULL, 248,
        'AVAILABLE', '2021-04-16', 0, 0, 'KG_CM'),
       (253, NULL, 'Nuno', 'Bento', NULL, NULL, 'f328a07c-473a-41b3-97ff-e7e7a89658b7', 2, NULL, NULL, NULL, 254,
        'AVAILABLE', '2021-04-18', 0, 0, 'KG_CM'),
       (296, NULL, 'Nuno', 'Bento', NULL, NULL, 'd7286e82-383c-4767-81b1-1ebaebd0c8ec', 2, NULL, NULL, NULL, 297,
        'AVAILABLE', '2021-05-12', 0, 0, 'KG_CM'),
       (299, NULL, 'test', 'etste', NULL, NULL, 'ca1b5020-1b75-4c42-8303-1ad47d1f85ef', 2, NULL, NULL, NULL, 300,
        'AVAILABLE', '2021-05-27', 0, 0, 'KG_CM');
UNLOCK TABLES;

--
-- Dumping data for table `Client_Measure_Weight`
--

LOCK TABLES `Client_Measure_Weight` WRITE;
INSERT INTO `Client_Measure_Weight`
VALUES (1, '2021-05-10', 70.65, NULL),
       (2, '2021-04-10', 71.65, NULL),
       (3, '2021-03-10', 72.65, NULL),
       (4, '2021-05-10', 41.14, NULL),
       (5, '2021-05-25', 80.65, NULL),
       (6, '2021-05-24', 78.6, NULL),
       (7, '2021-05-22', 77.89, NULL),
       (8, '2021-05-20', 76.98, NULL),
       (9, '2021-05-01', 76.98, NULL),
       (10, '2021-05-01', 40.5, NULL),
       (11, '2021-05-28', 80.5, NULL),
       (12, '2021-05-26', 90, NULL),
       (13, '2021-05-28', 24.1, NULL),
       (14, '2021-05-28', 20, NULL),
       (15, '2021-05-28', 0, NULL),
       (16, '2021-05-28', 0, NULL),
       (17, '2021-05-28', 0, NULL),
       (18, '2021-05-28', 0, NULL),
       (19, '2021-05-28', 0, NULL),
       (20, '2021-05-28', 60, NULL),
       (21, '2021-05-28', 60, NULL),
       (22, '2021-05-28', 60, NULL),
       (23, '2021-05-23', 60, NULL),
       (24, '2021-05-25', 65.6, NULL),
       (25, '2021-05-22', 70.6, NULL),
       (26, '2019-05-01', 70.6, 1),
       (27, '2021-05-28', 70, NULL),
       (28, '2021-05-24', 68.2, NULL),
       (29, '2021-05-23', 70, NULL),
       (30, '2021-05-30', 80, NULL),
       (31, '2021-05-27', 0.3, NULL),
       (32, '2021-05-28', 80, NULL),
       (33, '2021-05-30', 0, NULL),
       (34, '2021-05-28', 60, NULL),
       (35, '2021-05-31', 70, NULL),
       (36, '2021-05-25', 70, NULL),
       (37, '2021-05-19', 70, NULL),
       (38, '2021-06-01', 0, NULL),
       (39, '2021-06-01', 50.3, 1),
       (292, '2021-05-06', 31.98, NULL);
UNLOCK TABLES;

--
-- Dumping data for table `Daily_Task`
--

LOCK TABLES `Daily_Task` WRITE;
INSERT INTO `Daily_Task`
VALUES (44, '2021-12-05', 'Drink more tes', 'Drink Tea', '\0', '43d95c80-d71f-457e-872e-3624039e831e', 7, 4),
       (117, '2021-04-01', 'Drink water', 'Drink water', '\0', '2c98cfac-38ee-4717-ace0-d453dfd795f2', 16, 4),
       (118, '2021-04-02', 'Drink 1L of water', 'Drink water', '\0', '8656c04d-ebf6-4619-9468-45830013330e', 16, 4),
       (119, '2021-04-03', 'Drink 1L of water', 'Drink water', '\0', '177a9417-77fe-47de-bfeb-b2de3e862026', 16, 4),
       (120, '2021-04-04', 'Drink 1L of water', 'Drink water', '\0', 'a87b2c06-7789-48bf-8e9c-e09655e9f550', 16, 4),
       (127, '2021-04-03', 'Water', 'Drink Water', '\0', '3203e677-35ab-40ec-a898-24da1d182346', 25, 4),
       (129, '2021-04-06', 'Drink water', 'Drink Water', '', '88a9c5a7-566d-4c18-bc61-0c11f2a7b0b5', 1, 4),
       (130, '2021-04-03', '23232323', 'Test', '\0', 'b475079c-4013-4910-a421-06d48b627833', 1, 4),
       (131, '2021-04-04', '23232323', 'Test', '', '394a9285-12ca-4dea-bee2-753cbb1e4693', 1, 4),
       (132, '2021-04-04', '', 'Drink Water', '', '014f6c82-2a11-4ac3-a69b-265140042035', 1, 4),
       (133, '2021-04-04', '', 'Eat one egg', '', '214c3e5a-b9cd-416e-9efe-7a57b72f1559', 1, 4),
       (134, '2021-04-04', '', 'Think about motivations', '', '1d086474-8b4f-402a-8690-1c6e7bc1ad58', 1, 4),
       (135, '2021-04-05', 'Drink 1L of water ', 'Drink water', '', '43528e8e-adb1-4652-8ffe-7e8aa95ef03a', 1, 4),
       (137, '2021-04-05', '', 'Sleep 8 hours', '', '6c821643-3c8c-4bf2-b982-3c7aac098e28', 1, 4),
       (143, '2021-04-07', 'werwerwer', 'wrwer', '\0', '9ef3cef7-e95d-4bd6-885f-b0e81ab8a1a0', 1, 4),
       (144, '2021-04-07', 'werwerwer', 'wrwer', '\0', 'f20836a8-f48b-4ac8-ae16-d157f5e74563', 1, 4),
       (145, '2021-04-07', 'werwerwer', 'wrwer', '', '7c24f4f2-8917-4ee6-b081-fa665afe29ea', 1, 4),
       (146, '2021-04-07', 'werwerwer', 'wrwer', '', '9fe8a820-0c7a-4ce4-bba0-d4c3122d18b5', 1, 4),
       (147, '2021-04-08', 'Hello', 'Test', '\0', '1954e171-382f-436f-a97a-114576ddaa11', 10, 4),
       (151, '2021-04-08', 'Drink more water', 'Drink water', '', '5f71b003-dfc6-4810-9840-1cd838d4afb7', 148, 4),
       (152, '2021-04-09', 'Drink more water', 'Drink water', '', 'bfc46299-e051-4085-9af2-85bea2a69d39', 148, 4),
       (153, '2021-04-10', 'Drink more water', 'Drink water', '', 'f057cae1-eca8-4c2f-adcf-441602f50f53', 148, 4),
       (154, '2021-04-10', 'Drink more water', 'Drink tea', '', 'a6ce17ed-b4ca-4049-9808-97495b9e5824', 148, 4),
       (243, '2021-04-15', 'asdasdasdasda', 'sdasd', '', '38fc984d-4d2c-4269-945f-af2d142037cc', 25, 4),
       (244, '2021-04-15', 'asdasdasdasda', 'sdasd', '', 'af7b67ba-4183-4b1d-9a0a-ad8561f9a232', 25, 4),
       (245, '2021-04-15', 'asdasdasdasda', 'sdasd', '', '168b14db-e55e-4758-be82-465e02bbfc2f', 25, 4),
       (246, '2021-04-15', 'asdasdasdasda', 'sdasd', '\0', '3b6e60c6-76e0-4d35-bc17-6bb7552171c9', 25, 4),
       (304, '2021-06-03', 'Drink 2L of water', 'Drink Water', '\0', '8f12caf2-1cce-4eb2-83c0-13c6ed145530', 1, 28),
       (305, '2021-06-04', 'Drink 2L of water', 'Drink Water', '\0', '5a970af0-4389-4afe-b9e9-d68d517ecbe1', 1, 28),
       (309, '2021-06-03', 'Eddies Workout', 'Workout', '\0', '7c22bb0c-72fc-4784-90a6-b19d28968f5d', 1, 28),
       (310, '2021-06-04', 'Eddies Workout', 'Workout', '\0', '643f46b3-6fc4-40bb-85f7-a873f189863f', 1, 28),
       (311, '2021-06-05', 'Eddies Workout', 'Workout', '\0', 'c2a47962-dcb5-4647-b4dd-5c59beff0eec', 1, 28),
       (312, '2021-06-06', 'Eddies Workout', 'Workout', '\0', '0d97d57f-2b52-491d-84a5-727355d63c42', 1, 28),
       (313, '2021-06-07', 'Eddies Workout', 'Workout', '\0', '39f0960e-4fb1-40c2-8c67-f59d3f999991', 1, 28),
       (314, '2021-06-08', 'Eddies Workout', 'Workout', '\0', '8d3476c6-b7da-4916-b8d7-4c5649f590c7', 1, 28),
       (317, '2021-06-03', 'Eddies Workout2', 'Workout2', '\0', '51c5eefd-cd6b-4c2d-b106-968b2cb362c4', 1, 28),
       (318, '2021-06-04', 'Eddies Workout2', 'Workout2', '\0', 'e9edc42b-b107-4dde-928e-6406ad3adca4', 1, 28),
       (319, '2021-06-05', 'Eddies Workout2', 'Workout2', '\0', 'ffdf2123-637f-4b93-a749-5223c9ccddbc', 1, 28),
       (320, '2021-06-06', 'Eddies Workout2', 'Workout2', '\0', 'b0199163-9a60-4155-85c2-e531c7bfd610', 1, 28),
       (321, '2021-06-07', 'Eddies Workout2', 'Workout2', '\0', 'aceb3372-daa8-4436-9406-64502572c5d6', 1, 28),
       (322, '2021-06-08', 'Eddies Workout2', 'Workout2', '\0', '824c7f84-818b-4d1b-9b35-43b358b8d036', 1, 28),
       (325, '2021-06-03', 'Eddies Workout3', 'Workout3', '\0', 'fa6dad9a-6cb8-4dee-9b12-be67c804e2cf', 1, 28),
       (326, '2021-06-04', 'Eddies Workout3', 'Workout3', '\0', 'f1a5b034-a338-4b1b-ab75-2f61c2734d1c', 1, 28),
       (327, '2021-06-05', 'Eddies Workout3', 'Workout3', '\0', 'f25ea4f6-2998-4640-ac1d-cfd76daca973', 1, 28),
       (328, '2021-06-06', 'Eddies Workout3', 'Workout3', '\0', 'b8a2567e-cf8c-4fab-83ba-372dc8f923c6', 1, 28),
       (329, '2021-06-07', 'Eddies Workout3', 'Workout3', '\0', '716fcedc-674d-46bb-89ac-ad735c4dabfa', 1, 28),
       (330, '2021-06-08', 'Eddies Workout3', 'Workout3', '\0', '9e13e141-0709-4a97-80aa-86568e498de1', 1, 28),
       (333, '2021-06-03', 'Eddies Workout4', 'Workout4', '\0', 'bfcd47ab-251c-42d2-8bee-11b037b3bab9', 1, 28),
       (334, '2021-06-04', 'Eddies Workout4', 'Workout4', '\0', '6529dfbe-9bbd-4c0c-bdec-7fdb8bf43ed0', 1, 28),
       (335, '2021-06-05', 'Eddies Workout4', 'Workout4', '\0', 'a2a0c7ff-cfd0-4685-9fa4-92407c1c3ea5', 1, 28),
       (336, '2021-06-06', 'Eddies Workout4', 'Workout4', '\0', '30d06b04-b2ee-4a0e-8372-38470e884074', 1, 28),
       (337, '2021-06-07', 'Eddies Workout4', 'Workout4', '\0', '99618856-ee67-4f8d-9566-d03388a5b6c8', 1, 28),
       (338, '2021-06-08', 'Eddies Workout4', 'Workout4', '\0', '6b18971f-8724-4b1d-afbc-85962884a214', 1, 28),
       (341, '2021-06-03', 'Eddies Workout5', 'Workout5', '\0', '529a65dc-2523-4d80-a839-e1ed62eb58bc', 1, 28),
       (342, '2021-06-04', 'Eddies Workout5', 'Workout5', '\0', '8c2d189c-35c5-4997-8a43-9cd79f6bf749', 1, 28),
       (343, '2021-06-05', 'Eddies Workout5', 'Workout5', '\0', '9eaa4efd-a017-4b71-a7cc-e7a681de0343', 1, 28),
       (344, '2021-06-06', 'Eddies Workout5', 'Workout5', '\0', '1211eaeb-e7aa-4efa-b3d6-46d4781a5425', 1, 28),
       (345, '2021-06-07', 'Eddies Workout5', 'Workout5', '\0', '48727289-0378-4fd9-9aac-390dc93b640e', 1, 28),
       (346, '2021-06-08', 'Eddies Workout5', 'Workout5', '\0', '768453aa-27de-4208-96ed-27d9f93107d5', 1, 28),
       (349, '2021-06-03', 'testeeeeee', 'test', '\0', '94748386-03e0-46f5-bd3b-d1d5866d42ae', 1, 28),
       (351, '2021-06-02', 'Workout eddieazx\\zxzsDASDASDAS', 'Workout - Eddies', '\0',
        '685d6b03-e4cf-4485-9c73-089ec0809ef0', 1, 28),
       (352, '2021-06-03', 'Workout eddie', 'Workout - Eddies', '\0', 'fd326452-1f3d-4aad-a320-891771f77ad4', 1, 28),
       (353, '2021-06-04', 'Workout eddie', 'Workout - Eddies', '\0', 'b2b78294-3d9d-4163-889a-c929f4fbccf8', 1, 28),
       (354, '2021-06-05', 'Workout eddie', 'Workout - Eddies', '\0', 'b81509c4-0e94-47b2-b0b2-bc6bf711b725', 1, 28),
       (355, '2021-06-06', 'Workout eddie', 'Workout - Eddies', '\0', '70722a09-5e90-435a-a2dc-07cfbdde9bf6', 1, 28),
       (356, '2021-06-07', 'Workout eddie', 'Workout - Eddies', '\0', '7bb086b0-87e3-4f3d-9847-55d968514dd3', 1, 28),
       (357, '2021-06-22', 'teste 22222', 'Test', '\0', 'fec28cf1-a746-4da8-99b6-1d8d775ee9a2', 1, 28),
       (358, '2021-06-23', 'teste 22222', 'Test', '\0', 'a97c4b96-b3f1-4f17-a2e0-31bf86f71294', 1, 28),
       (359, '2021-06-24', 'teste 22222', 'Test', '\0', '9cbe4604-a188-4b35-825a-0e4f6cd56109', 1, 28),
       (360, '2021-06-25', 'teste 22222', 'Test', '\0', '06792d2c-18a0-466f-bdf1-ee7d74d71d83', 1, 28),
       (361, '2021-06-26', 'teste 22222', 'Test', '\0', 'a596f222-b754-40c3-8350-d9c435216cc8', 1, 28),
       (362, '2021-06-27', 'teste 22222', 'Test', '\0', 'b72284f6-40cf-4b3f-bbad-02c058bc5c40', 1, 28),
       (363, '2021-06-28', 'teste 22222', 'Test', '\0', 'e308faf4-1159-488f-9209-1c52889723cc', 1, 28);
UNLOCK TABLES;

-- Dump completed on 2021-06-03  7:50:10
