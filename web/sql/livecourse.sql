-- phpMyAdmin SQL Dump
-- version 3.5.6
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 02, 2013 at 03:52 PM
-- Server version: 5.5.30-log
-- PHP Version: 5.3.17

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `livecourse`
--

-- --------------------------------------------------------

--
-- Table structure for table `lc_authentication`
--

CREATE TABLE IF NOT EXISTS `lc_authentication` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID of authentication token',
  `user_id` int(11) NOT NULL COMMENT 'ID of user',
  `token` varchar(16) CHARACTER SET latin1 NOT NULL COMMENT 'Random string token',
  `lastused` int(11) NOT NULL COMMENT 'Last time this token was used to successfully authenticate',
  `device` int(11) NOT NULL COMMENT 'identifier for device that this authentication took place using',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_buildings`
--

CREATE TABLE IF NOT EXISTS `lc_buildings` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identifier',
  `institution_id` int(11) NOT NULL COMMENT 'ID of institution where this building resides',
  `name` varchar(512) NOT NULL COMMENT 'Name of the building',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_chats`
--

CREATE TABLE IF NOT EXISTS `lc_chats` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Room ID, primary index',
  `id_string` varchar(12) NOT NULL COMMENT 'Randomized string for identifying the room to the client',
  `subject_id` int(11) NOT NULL COMMENT 'ID of subject this room belongs to',
  `course_number` smallint(6) NOT NULL COMMENT 'Course number',
  `name` varchar(100) NOT NULL COMMENT 'Name of the room / class',
  `institution_id` int(11) NOT NULL COMMENT 'identifier of the institution this room is offered at',
  `room_id` int(11) NOT NULL COMMENT 'room identifier from lc_rooms',
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_chat_messages`
--

CREATE TABLE IF NOT EXISTS `lc_chat_messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID of message',
  `chat_id` int(11) NOT NULL COMMENT 'ID of chat room message is destined for',
  `user_id` int(11) NOT NULL COMMENT 'ID of user that sent this message',
  `send_time` int(11) NOT NULL COMMENT 'Time that this message was sent in UNIX Epoch',
  `message_string` varchar(2048) CHARACTER SET latin1 NOT NULL COMMENT 'Message content',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_chat_messages_flagged`
--

CREATE TABLE IF NOT EXISTS `lc_chat_messages_flagged` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'unique id of the flagged message entry',
  `message_id` int(11) NOT NULL COMMENT 'ID of the message flagged',
  `reporter_id` int(11) NOT NULL COMMENT 'ID of the user who flagged the message',
  `reason` varchar(1024) CHARACTER SET latin1 NOT NULL COMMENT 'Reason for reporting the message',
  `time_submitted` int(11) NOT NULL COMMENT 'Time at which the user reported the message. Seconds since unix epoch.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_chat_participants`
--

CREATE TABLE IF NOT EXISTS `lc_chat_participants` (
  `chat_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `permissions` int(11) NOT NULL DEFAULT '0' COMMENT '0 = default, 1 = admin',
  `jointime` int(11) NOT NULL COMMENT 'Time the user joined this particular chat in UNIX epoch'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lc_gcm_users`
--

CREATE TABLE IF NOT EXISTS `lc_gcm_users` (
  `user_id` int(11) NOT NULL COMMENT 'ID of the user who registered the device',
  `gcm_regid` text CHARACTER SET latin1,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lc_institutions`
--

CREATE TABLE IF NOT EXISTS `lc_institutions` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identification Number',
  `name` varchar(255) NOT NULL COMMENT 'Name of institution',
  `zip` varchar(5) NOT NULL COMMENT 'Zip code of institution location',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_rooms`
--

CREATE TABLE IF NOT EXISTS `lc_rooms` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Room identifier',
  `building_id` int(11) NOT NULL COMMENT 'ID of building this room is located in',
  `room_number` varchar(64) NOT NULL COMMENT 'Room number in building',
  `room_name` varchar(128) NOT NULL COMMENT 'Room name, alias',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_subjects`
--

CREATE TABLE IF NOT EXISTS `lc_subjects` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identifier',
  `name` varchar(256) NOT NULL COMMENT 'Name of subject',
  `code` varchar(5) NOT NULL COMMENT 'Code (Computer science = CS, etc)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
