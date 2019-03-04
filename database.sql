CREATE TABLE IF NOT EXISTS `iaevange`.`Player` (
  `playerId` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(60) NULL,
  `points` INT NULL,
  `activeTurn` TINYINT(1) NOT NULL DEFAULT 0,
  `password` VARCHAR(45) NULL,
  `female` TINYINT(1) NULL,
  `birthyear` INT NULL,
  PRIMARY KEY (`playerId`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `iaevange`.`Category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `iaevange`.`Category` (
  `categoryId` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`categoryId`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `iaevange`.`Questions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `iaevange`.`Questions` (
  `questionId` INT NOT NULL AUTO_INCREMENT,
  `questionText` TEXT NULL,
  `categoryId` INT NOT NULL,
  PRIMARY KEY (`questionId`),
  CONSTRAINT `categoryId`
    FOREIGN KEY (`questionId`)
    REFERENCES `iaevange`.`Category` (`categoryId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `iaevange`.`Alternative`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `iaevange`.`Alternative` (
  `answerId` INT NOT NULL AUTO_INCREMENT,
  `presidentId` INT NULL,
  `score` INT NULL,
  `questionId` INT NOT NULL,
  PRIMARY KEY (`answerId`),
  INDEX `questionId_idx` (`questionId` ASC),
  CONSTRAINT `questionId`
    FOREIGN KEY (`questionId`)
    REFERENCES `iaevange`.`Questions` (`questionId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `iaevange`.`Game`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `iaevange`.`Game` (
  `gameId` INT NOT NULL AUTO_INCREMENT,
  `player1` INT NULL,
  `player2` INT NULL,
  `p1Points` INT NULL,
  `p2Points` INT NULL,
  `categoryId` INT NULL,
  `question1` INT NULL,
  `question2` INT NULL,
  `question3` INT NULL,
  PRIMARY KEY (`gameId`),
  INDEX `player1_idx` (`player1` ASC),
  INDEX `player2_idx` (`player2` ASC),
  INDEX `question1_idx` (`question1` ASC),
  INDEX `question2_idx` (`question2` ASC),
  INDEX `question3_idx` (`question3` ASC),
  CONSTRAINT `player1`
    FOREIGN KEY (`player1`)
    REFERENCES `iaevange`.`Player` (`playerId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `player2`
    FOREIGN KEY (`player2`)
    REFERENCES `iaevange`.`Player` (`playerId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `category`
    FOREIGN KEY ()
    REFERENCES `iaevange`.`Category` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `question1`
    FOREIGN KEY (`question1`)
    REFERENCES `iaevange`.`Questions` (`questionId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `question2`
    FOREIGN KEY (`question2`)
    REFERENCES `iaevange`.`Questions` (`questionId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `question3`
    FOREIGN KEY (`question3`)
    REFERENCES `iaevange`.`Questions` (`questionId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;