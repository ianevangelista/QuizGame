DROP TABLE IF EXISTS `Player`;
DROP TABLE IF EXISTS `Category`;
DROP TABLE IF EXISTS `Question`;
DROP TABLE IF EXISTS `Alternative`;
DROP TABLE IF EXISTS `Game`;

CREATE TABLE IF NOT EXISTS `iaevange`.`Player` (
  `username` VARCHAR(15) NOT NULL,
  `email` VARCHAR(60) NULL,
  `points` INT NULL,
  `online` TINYINT(1) NOT NULL DEFAULT 0,
  `password` VARCHAR(100) NULL,
  `salt` VARCHAR(100) NULL,
  `female` TINYINT(1) NULL,
<<<<<<< HEAD
  `birthyear` INT(4) NULL,
  `gameId` INT NULL,
  PRIMARY KEY (`username`),
  FOREIGN KEY(`gameId`) REFERENCES `iaevange`.`Game` (`gameId`)
=======
  `birthyear` INT NULL,
  PRIMARY KEY (`username`)
>>>>>>> Endring
);


-- -----------------------------------------------------
-- Table `iaevange`.`Category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `iaevange`.`Category` (
  `categoryId` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`categoryId`)
);


-- -----------------------------------------------------
-- Table `iaevange`.`Question`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `iaevange`.`Question` (
  `questionId` INT NOT NULL AUTO_INCREMENT,
  `questionText` TEXT NULL,
  `categoryId` INT NOT NULL,
  PRIMARY KEY (`questionId`),
  FOREIGN KEY (`questionId`) REFERENCES `iaevange`.`Category` (`categoryId`)
);


-- -----------------------------------------------------
-- Table `iaevange`.`Alternative`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `iaevange`.`Alternative` (
  `answerId` INT NOT NULL AUTO_INCREMENT,
  `presidentId` INT NULL,
  `score` INT NULL,
  `questionId` INT NOT NULL,
  PRIMARY KEY (`answerId`),
  FOREIGN KEY (`questionId`) REFERENCES `iaevange`.`Question` (`questionId`)
);


-- -----------------------------------------------------
-- Table `iaevange`.`Game`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `iaevange`.`Game` (
  `gameId` INT NOT NULL AUTO_INCREMENT,
  `player1` VARCHAR(15) NULL,
  `player2` VARCHAR(15) NULL,
  `p1Points` INT NULL,
  `p2Points` INT NULL,
  `categoryId` INT NULL,
  `question1` INT NULL,
  `question2` INT NULL,
  `question3` INT NULL,
  `p1Finished` TINYINT(1) NULL,
  `p2Finished` TINYINT(1) NULL,
  PRIMARY KEY (`gameId`),
  FOREIGN KEY (`player1`) REFERENCES `iaevange`.`Player` (`username`),
  FOREIGN KEY (`player2`) REFERENCES `iaevange`.`Player` (`username`),
  FOREIGN KEY (`categoryId`) REFERENCES `iaevange`.`Category` (`categoryId`),
  FOREIGN KEY (`question1`) REFERENCES `iaevange`.`Question` (`questionId`),
  FOREIGN KEY (`question2`) REFERENCES `iaevange`.`Question` (`questionId`),
  FOREIGN KEY (`question3`) REFERENCES `iaevange`.`Question` (`questionId`)
);