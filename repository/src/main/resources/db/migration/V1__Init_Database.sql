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
) ENGINE = InnoDB;

--
-- Table structure for table `hibernate_sequence`
--

CREATE TABLE `hibernate_sequence`
(
    `next_val` bigint DEFAULT NULL
) ENGINE = InnoDB;

--
-- Table structure for table `Client_Type`
--

CREATE TABLE `Client_Type`
(
    `id`   bigint       NOT NULL,
    `type` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

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
) ENGINE = InnoDB;

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
) ENGINE = InnoDB;

-- Table structure for table `Country_Config`
--

CREATE TABLE `Country_Config`
(
    `id`            bigint       NOT NULL,
    `countryCode`   varchar(255) NOT NULL,
    `externalValue` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

--
-- Table structure for table `Gender_Config`
--

CREATE TABLE `Gender_Config`
(
    `id`            bigint       NOT NULL,
    `externalValue` varchar(255) NOT NULL,
    `genderCode`    varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

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
) ENGINE = InnoDB;

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
) ENGINE = InnoDB;

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
) ENGINE = InnoDB;

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
) ENGINE = InnoDB;

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
VALUES (387, '2021-11-05 22:29:04.222063', '8d01cd4b-6ca8-499b-b8a2-e6088f20fcca'),
       (392, '2021-11-05 22:18:49.760407', '956e3742-bc56-4f20-b077-7688d360d5fa'),
       (439, '2021-11-15 20:46:34.359074', '5ae953fe-c831-4f2e-8c8c-bc80ff440f44');
UNLOCK TABLES;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
INSERT INTO `User`
VALUES (386, 'caroline.couric@gmail.com',
        'szhWVIv3dCqnoSKFMDfj7+soUr4d7wBxdmFoMpzvfHzjcXWcd51mRyOZbxZ0t3KhTSB527kVYpOpo+jrtqqT5w==', 387,
        'WMJp+H3bbM7ONiPmoJjH'),
       (391, 'vidar.rahman@gmail.com',
        'dpMpzxuGDq19+5XRW6x3KXOHrOT8Dlzu+ThPFrzVZepMjZY2ok8hxvDXuvLrYTd0g8YOnhqeNfLwwWzclXY+5g==', 392,
        '4k4jy+gCE/p9lT+9G+7m'),
       (438, 'nbento.neves@gmail.com',
        'Jl2ZwP6KDykj2nO6+Hh/shwKLqXa+xdHxqPRsnhaP0b6DXQXX12pJMbA0XGmrz6Xu/TL7WUA0q9RPY8A9vk0eQ==', 439,
        'V6PLOUlbqeeCRDJcStHg');
UNLOCK TABLES;

--
-- Dumping data for table `Coach`
--

LOCK TABLES `Coach` WRITE;
INSERT INTO `Coach`
VALUES (385, null, 'Caroline', 'Couric', null, null, 'f6389f0a-0139-4288-910e-a5251230c3dd', 1, null, null, 386,
        '2021-11-05'),
       (437, null, 'Nuno', 'Bento', null, null, '01d3f5c2-7981-4b16-95c6-20348a50772b', 1, null, null, 438,
        '2021-11-09');
UNLOCK TABLES;

--
-- Dumping data for table `Client`
--
LOCK TABLES `Client` WRITE;
INSERT INTO `Client`
VALUES (389, null, 'Vidar', 'Rahman', null, null, '1fbbbb55-59d2-47a4-8040-b3753d2d0f1c', 2, 385, 234, 2, 391,
        'ACCEPTED', '2021-11-05', 1.75, 70, 'KG_CM');
UNLOCK TABLES;

--
-- Dumping data for table `Client_Measure_Weight`
--

LOCK TABLES `Client_Measure_Weight` WRITE;
INSERT INTO `Client_Measure_Weight`
VALUES (40, '2021-10-01', 70, null),
       (41, '2021-09-01', 70, 389),
       (42, '2021-10-01', 70, 389),
       (43, '2021-10-10', 75.5, 389),
       (44, '2021-10-15', 80, 389),
       (45, '2021-10-25', 77.6, 389),
       (46, '2021-10-29', 79, 389);
UNLOCK TABLES;

--
-- Dumping data for table `Daily_Task`
--

LOCK TABLES `Daily_Task` WRITE;
INSERT INTO `Daily_Task`
VALUES (401, '2021-10-31', 'Should drink 2L of water', 'Drink water', false, '3b701126-bdef-411a-b65a-d78d80da58c6',
        389, 385),
       (402, '2021-11-01', 'Should drink 2L of water', 'Drink water', false, 'd13dbc01-136d-40ae-9764-8e606e82e689',
        389, 385),
       (403, '2021-11-02', 'Should drink 2L of water', 'Drink water', false, '0be65501-828d-48ab-890a-b9590222c000',
        389, 385),
       (404, '2021-11-03', 'Should drink 2L of water', 'Drink water', false, '16c745f7-a552-44ab-9399-6b868b2972ba',
        389, 385),
       (405, '2021-11-04', 'Should drink 2L of water', 'Drink water', false, 'd28ebb55-c757-4c7f-ad8b-20fd24144ccb',
        389, 385),
       (406, '2021-11-05', 'Should drink 2L of water', 'Drink water', false, '4d509043-2e0e-4842-b0b3-664e10805acb',
        389, 385),
       (407, '2021-11-06', 'Should drink 2L of water', 'Drink water', false, '40b5f4ec-087a-43a5-a666-48ed8218f49f',
        389, 385),
       (409, '2021-11-05', '', 'Take one protein scoop', false, '9593e3ed-0b1e-4c80-9890-fe29f3775647', 389, 385),
       (410, '2021-11-02', '', 'Take one protein scoop', false, '8a6530a1-8d9b-4e87-9209-648765d20cd0', 389, 385),
       (411, '2021-11-02', '', 'You can eat one sweet', false, '7fa4f4aa-2c1c-4c5e-87cd-2b40720aa204', 389, 385);
UNLOCK TABLES;

-- Dump completed on 2021-06-03  7:50:10
