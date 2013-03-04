-- phpMyAdmin SQL Dump
-- version 3.5.6
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 04, 2013 at 03:12 AM
-- Server version: 5.5.30
-- PHP Version: 5.4.11

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `hayden_livecourse`
--

-- --------------------------------------------------------

--
-- Table structure for table `lc_rooms`
--

CREATE TABLE IF NOT EXISTS `lc_rooms` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Room ID, primary index',
  `id_string` varchar(12) NOT NULL COMMENT 'Randomized string for identifying the room to the client',
  `name` varchar(100) NOT NULL COMMENT 'Name of the room / class',
  `building` varchar(100) NOT NULL COMMENT 'Name of the building this class takes place in',
  `room` varchar(100) NOT NULL COMMENT 'Room that the class takes place in',
  `start_time` int(5) NOT NULL COMMENT 'Time that the class starts at in format MINUTES SINCE MIDNIGHT UTC',
  `end_time` int(5) NOT NULL COMMENT 'Time that the class ends at in format MINUTES SINCE MIDNIGHT UTC',
  `start_date` date NOT NULL COMMENT 'Date that the class begins',
  `end_date` date NOT NULL COMMENT 'Date that the class ends',
  `dow_monday` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Class occurs on a Monday',
  `dow_tuesday` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Class occurs on a Tuesday',
  `dow_wednesday` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Class occurs on a Wednesday',
  `dow_thursday` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Class occurs on a Thursday',
  `dow_friday` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Class occurs on a Friday',
  `dow_saturday` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Class occurs on a Saturday',
  `dow_sunday` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Class occurs on a Sunday',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_room_participants`
--

CREATE TABLE IF NOT EXISTS `lc_room_participants` (
  `room_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `permissions` int(11) NOT NULL DEFAULT '0' COMMENT '0 = default, 1 = admin'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lc_users`
--

CREATE TABLE IF NOT EXISTS `lc_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'User ID, primary index',
  `email` varchar(255) NOT NULL COMMENT 'User''s e-mail address',
  `password` varchar(255) NOT NULL COMMENT 'User''s sha1 encrypted password',
  `display_name` varchar(255) NOT NULL,
  `jointime` int(11) NOT NULL COMMENT 'User''s time of sign-up in UNIX epoch',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
