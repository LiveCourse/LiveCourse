-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 23, 2013 at 03:40 AM
-- Server version: 5.5.29
-- PHP Version: 5.4.6-1ubuntu1.2

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=226 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_buildings`
--

CREATE TABLE IF NOT EXISTS `lc_buildings` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identifier',
  `institution_id` int(11) NOT NULL COMMENT 'ID of institution where this building resides',
  `name` varchar(512) NOT NULL COMMENT 'Name of the building',
  `short_name` varchar(10) NOT NULL COMMENT 'Shortened name (ex. LWSN)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=86 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_chat_files`
--

CREATE TABLE IF NOT EXISTS `lc_chat_files` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT 'User ID of the User who uploaded the file',
  `chat_id` int(11) NOT NULL COMMENT 'The Chat Room that the file was uploaded to',
  `filename` varchar(256) NOT NULL COMMENT 'The Name of the File that was uploaded',
  `original_name` varchar(256) NOT NULL,
  `size` int(11) NOT NULL,
  `message_id` int(11) NOT NULL COMMENT 'ID of the associated message',
  `uploaded_at` int(11) NOT NULL COMMENT 'Timestamp in EPOCH of when the file was uploaded',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=11 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_chat_messages`
--

CREATE TABLE IF NOT EXISTS `lc_chat_messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID of message',
  `class_id` int(11) NOT NULL COMMENT 'ID of chat room message is destined for',
  `user_id` int(11) NOT NULL COMMENT 'ID of user that sent this message',
  `send_time` int(11) NOT NULL COMMENT 'Time that this message was sent in UNIX Epoch',
  `message_string` varchar(2048) NOT NULL COMMENT 'Message content',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=109 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_chat_messages_flagged`
--

CREATE TABLE IF NOT EXISTS `lc_chat_messages_flagged` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'unique id of the flagged message entry',
  `message_id` int(11) NOT NULL COMMENT 'ID of the message flagged',
  `reporter_id` int(11) NOT NULL COMMENT 'ID of the user who flagged the message',
  `reason` varchar(1024) NOT NULL COMMENT 'Reason for reporting the message',
  `time_submitted` int(11) NOT NULL COMMENT 'Time at which the user reported the message. Seconds since unix epoch.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_classes`
--

CREATE TABLE IF NOT EXISTS `lc_classes` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Room ID, primary index',
  `id_string` varchar(12) NOT NULL COMMENT 'Randomized string for identifying the room to the client',
  `subject_id` int(11) NOT NULL COMMENT 'ID of subject this room belongs to',
  `course_number` int(11) NOT NULL COMMENT 'Course number',
  `name` varchar(100) NOT NULL COMMENT 'Name of the room / class',
  `institution_id` int(11) NOT NULL COMMENT 'identifier of the institution this room is offered at',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=11292 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_gcm_users`
--

CREATE TABLE IF NOT EXISTS `lc_gcm_users` (
  `user_id` int(11) NOT NULL COMMENT 'ID of the user who registered the device',
  `gcm_regid` text,
  `created_at` int(11) NOT NULL,
  `device_id` varchar(16) NOT NULL
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=653 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_sections`
--

CREATE TABLE IF NOT EXISTS `lc_sections` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary ID',
  `id_string` varchar(32) NOT NULL COMMENT 'Random string ID',
  `class_id` int(11) NOT NULL COMMENT 'ID of class this is a section of from lc_classes table',
  `type` varchar(64) NOT NULL COMMENT 'Type of section - Lecture, Recitation, Laboratory, etc.',
  `crn` int(11) NOT NULL COMMENT 'CRN number',
  `section` varchar(10) NOT NULL COMMENT 'Section number',
  `room_id` int(11) NOT NULL COMMENT 'ID of the room this section takes place in from lc_rooms',
  `dow_monday` tinyint(1) NOT NULL,
  `dow_tuesday` tinyint(1) NOT NULL,
  `dow_wednesday` tinyint(1) NOT NULL,
  `dow_thursday` tinyint(1) NOT NULL,
  `dow_friday` tinyint(1) NOT NULL,
  `dow_saturday` tinyint(1) NOT NULL,
  `dow_sunday` tinyint(1) NOT NULL,
  `start_time` int(11) NOT NULL,
  `end_time` int(11) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `capacity` int(11) NOT NULL,
  `instructor` varchar(256) NOT NULL,
  `notes` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=16249 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_section_participants`
--

CREATE TABLE IF NOT EXISTS `lc_section_participants` (
  `section_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `permissions` int(11) NOT NULL DEFAULT '0' COMMENT '0 = default, 1 = admin',
  `jointime` int(11) NOT NULL COMMENT 'Time the user joined this particular chat in UNIX epoch'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lc_subjects`
--

CREATE TABLE IF NOT EXISTS `lc_subjects` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identifier',
  `name` varchar(256) NOT NULL COMMENT 'Name of subject',
  `code` varchar(5) NOT NULL COMMENT 'Code (Computer science = CS, etc)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=126 ;

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
  `color_preference` smallint(6) NOT NULL DEFAULT '0' COMMENT 'User''s UI color preference',
  `time_lastfocus` int(11) NOT NULL DEFAULT '0' COMMENT 'Time of the user''s last message / focus. Used to determine online status.',
  `time_lastrequest` int(11) NOT NULL DEFAULT '0' COMMENT 'Time of user''s last active request to the server. Used to determine online status.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_users_ignored`
--

CREATE TABLE IF NOT EXISTS `lc_users_ignored` (
  `user_id` int(11) NOT NULL,
  `ignore_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lc_wp_users`
--

CREATE TABLE IF NOT EXISTS `lc_wp_users` (
  `user_id` int(11) NOT NULL COMMENT 'User ID of the user who registered the phone',
  `device_id` varchar(255) NOT NULL COMMENT 'Unique ID identifying the phone',
  `push_url` varchar(512) NOT NULL COMMENT 'URL to send PUSH notifications to',
  `channel` tinyint(4) NOT NULL COMMENT '0 = toast, 1 = raw',
  `timeadded` int(11) NOT NULL COMMENT 'Time this device was registered'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sections_tmp`
--

CREATE TABLE IF NOT EXISTS `sections_tmp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `crn` int(11) NOT NULL,
  `subj` varchar(16) NOT NULL,
  `course_number` int(11) NOT NULL,
  `section` varchar(16) NOT NULL,
  `name` varchar(256) NOT NULL,
  `dow_monday` tinyint(1) NOT NULL,
  `dow_tuesday` tinyint(1) NOT NULL,
  `dow_wednesday` tinyint(1) NOT NULL,
  `dow_thursday` tinyint(1) NOT NULL,
  `dow_friday` tinyint(1) NOT NULL,
  `dow_saturday` tinyint(1) NOT NULL,
  `dow_sunday` tinyint(1) NOT NULL,
  `time_start` int(11) NOT NULL,
  `time_end` int(11) NOT NULL,
  `date_start` date NOT NULL,
  `date_end` date NOT NULL,
  `capacity` int(11) NOT NULL,
  `instructor` varchar(256) NOT NULL,
  `location` varchar(128) NOT NULL,
  `type` varchar(128) NOT NULL,
  `notes` text NOT NULL,
  `link_self` varchar(16) NOT NULL,
  `link_other` varchar(16) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lc_notes`
--

CREATE TABLE IF NOT EXISTS `lc_notes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `class_id` int(11) NOT NULL,
  `parent_note_id` int(11) NOT NULL DEFAULT '0',
  `linked_message_id` int(11) NOT NULL DEFAULT '0',
  `text` text NOT NULL,
  `timeadded` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
