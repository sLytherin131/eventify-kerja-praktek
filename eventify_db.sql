-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 29, 2025 at 07:46 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `eventify_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `whatsapp_number` varchar(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`whatsapp_number`, `name`, `email`, `password`, `created_at`) VALUES
('081217724337', 'Isaaccc', 'isaacyeremia@gmail.com', 'newpassword123', 1750655641688),
('081217850127', 'Claudio', 'claudio@example.com', 'claudio12345', 1748226011913),
('081217850128', 'Admin Name', 'admin@example.com', '$2a$10$RmOImTvwtpARje9yD96Ww.hv.9fOZ/YMo0gt0CkiOByx5j3zVBhAi', 1747202922140),
('081217860128', 'Ricky Junianto Wijaya', 'rickywijaya047@gmail.com', 'apakabar123', 1750913799426),
('0812717724337', 'Isaaccc', 'isaacyeremia@gmail.com', 'newsecret123', 1750655580924),
('081278238130', 'Kevin', '081278238130', 'kevin12345', 1750915062752),
('08178555902', 'Dante', 'dante@example.com', 'dante12345', 1748195533742),
('0987654321', 'Admin Name', 'admin@example.com', '$2a$10$LNvPZ0//76SjbI4AwcKFge1RjdN5Pi8HdQwojbUbkNWiyekC8QiKq', 1747204233244),
('112233445566', 'Petrus', 'admin@example.com', '$2a$10$s7F7BjdEzxzoPjqh8wI20OVYHvfobTc3Qns7mRbuVn3hk5iensjOS', 1747282779342),
('12345432112345', 'Admin Name', 'admin@example.com', '$2a$10$oWEk4lMtcs0D5fAmJol7TeP7lZ8kf7RieWf8wf.pyWmcHGqM/avGS', 1747334792752),
('1234567890', 'Admin Name', 'admin@example.com', '$2a$10$LPI0yOn1MWM3H8VwZOw1XuTKZV7F9UStR4hP70xKsPESsUaO6o/ji', 1746958027123),
('5432109876', 'Admin Name', 'admin@example.com', '$2a$10$YHAVnsrhFZfSADWTEBnq5.qIsMC0z7VeaeYF9hZ8btc9S62zb2BOa', 1747204289243),
('99000128390', 'Benaya', 'benaya@example.com', 'benaya12345', 1748196617781),
('kevin123@gmail.com', 'Kevin', 'kevin123@gmail.com', 'kevin12345', 1750915404473);

-- --------------------------------------------------------

--
-- Table structure for table `events`
--

CREATE TABLE `events` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `start_time` bigint(20) NOT NULL,
  `end_time` bigint(20) NOT NULL,
  `created_by` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`id`, `name`, `description`, `start_time`, `end_time`, `created_by`) VALUES
(59, 'test', 'test', 1751152560000, 1751163420000, '081217860128'),
(60, 'Halo', 'Halo', 1751241840000, 1751252640000, '081217860128');

-- --------------------------------------------------------

--
-- Table structure for table `event_members`
--

CREATE TABLE `event_members` (
  `id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL,
  `member_whatsapp` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `event_members`
--

INSERT INTO `event_members` (`id`, `event_id`, `member_whatsapp`) VALUES
(70, 59, '087810000239'),
(71, 60, '087810000239');

-- --------------------------------------------------------

--
-- Table structure for table `event_tasks`
--

CREATE TABLE `event_tasks` (
  `id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL,
  `description` text NOT NULL,
  `task_type` varchar(50) NOT NULL,
  `created_at` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `event_tasks`
--

INSERT INTO `event_tasks` (`id`, `event_id`, `description`, `task_type`, `created_at`) VALUES
(104, 59, 'halo', 'urgent', 1751152653463),
(105, 60, 'Tes', 'normal', 1751155505527);

-- --------------------------------------------------------

--
-- Table structure for table `members`
--

CREATE TABLE `members` (
  `whatsapp_number` varchar(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `created_at` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `members`
--

INSERT INTO `members` (`whatsapp_number`, `name`, `created_at`) VALUES
('00007812900', 'Isaac', 1748196553975),
('00007812902', 'Member Name', 1749335873928),
('081217724337', 'Isaac', 1748844994187),
('087810000239', 'test', 1748801862463),
('0895267213087', 'Claudio Erlisto C', 1750918246271),
('089526721393', 'Claudio Erlisto', 1748845087414),
('0895366578789', 'Kevin Han', 1750918962136),
('1234567890', 'Ricky J', 1747282068775),
('6281936660813', 'Member Name', 1748804046986),
('6287810000239', 'Test', 1748802972690),
('6289616830735', 'Petrus K', 1749432137174);

-- --------------------------------------------------------

--
-- Table structure for table `personal_tasks`
--

CREATE TABLE `personal_tasks` (
  `id` int(11) NOT NULL,
  `admin_whatsapp` varchar(20) NOT NULL,
  `description` text NOT NULL,
  `task_type` varchar(50) NOT NULL,
  `created_at` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `personal_tasks`
--

INSERT INTO `personal_tasks` (`id`, `admin_whatsapp`, `description`, `task_type`, `created_at`) VALUES
(7, '08178555902', 'Complete monthly report', 'work', 1748175025252),
(20, '081217724337', 'test 1', 'Urgent', 1750670625990),
(21, '081217860128', 'halo', 'Urgent', 1750819930163),
(23, 'kevin123@gmail.com', 'test', 'Work', 1750915993274),
(24, '081217860128', 'Test', 'Personal', 1750916961253);

-- --------------------------------------------------------

--
-- Table structure for table `task_summary_cache`
--

CREATE TABLE `task_summary_cache` (
  `id` int(11) NOT NULL,
  `admin_whatsapp` varchar(20) DEFAULT NULL,
  `month` year(4) DEFAULT NULL,
  `month_num` int(11) DEFAULT NULL,
  `personal_count` int(11) DEFAULT NULL,
  `work_count` int(11) DEFAULT NULL,
  `urgent_count` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `weekly_event_summary`
--

CREATE TABLE `weekly_event_summary` (
  `id` int(11) NOT NULL,
  `week_start` date DEFAULT NULL,
  `week_end` date DEFAULT NULL,
  `event_id` int(11) DEFAULT NULL,
  `participant_count` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`whatsapp_number`);

--
-- Indexes for table `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `event_members`
--
ALTER TABLE `event_members`
  ADD PRIMARY KEY (`id`),
  ADD KEY `event_id` (`event_id`),
  ADD KEY `member_whatsapp` (`member_whatsapp`);

--
-- Indexes for table `event_tasks`
--
ALTER TABLE `event_tasks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `event_id` (`event_id`);

--
-- Indexes for table `members`
--
ALTER TABLE `members`
  ADD PRIMARY KEY (`whatsapp_number`);

--
-- Indexes for table `personal_tasks`
--
ALTER TABLE `personal_tasks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `admin_whatsapp` (`admin_whatsapp`);

--
-- Indexes for table `task_summary_cache`
--
ALTER TABLE `task_summary_cache`
  ADD PRIMARY KEY (`id`),
  ADD KEY `admin_whatsapp` (`admin_whatsapp`);

--
-- Indexes for table `weekly_event_summary`
--
ALTER TABLE `weekly_event_summary`
  ADD PRIMARY KEY (`id`),
  ADD KEY `event_id` (`event_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `events`
--
ALTER TABLE `events`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;

--
-- AUTO_INCREMENT for table `event_members`
--
ALTER TABLE `event_members`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=72;

--
-- AUTO_INCREMENT for table `event_tasks`
--
ALTER TABLE `event_tasks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=106;

--
-- AUTO_INCREMENT for table `personal_tasks`
--
ALTER TABLE `personal_tasks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `task_summary_cache`
--
ALTER TABLE `task_summary_cache`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `weekly_event_summary`
--
ALTER TABLE `weekly_event_summary`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `events`
--
ALTER TABLE `events`
  ADD CONSTRAINT `events_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `admins` (`whatsapp_number`);

--
-- Constraints for table `event_members`
--
ALTER TABLE `event_members`
  ADD CONSTRAINT `event_members_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`),
  ADD CONSTRAINT `event_members_ibfk_2` FOREIGN KEY (`member_whatsapp`) REFERENCES `members` (`whatsapp_number`);

--
-- Constraints for table `event_tasks`
--
ALTER TABLE `event_tasks`
  ADD CONSTRAINT `event_tasks_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`);

--
-- Constraints for table `personal_tasks`
--
ALTER TABLE `personal_tasks`
  ADD CONSTRAINT `personal_tasks_ibfk_1` FOREIGN KEY (`admin_whatsapp`) REFERENCES `admins` (`whatsapp_number`);

--
-- Constraints for table `task_summary_cache`
--
ALTER TABLE `task_summary_cache`
  ADD CONSTRAINT `task_summary_cache_ibfk_1` FOREIGN KEY (`admin_whatsapp`) REFERENCES `admins` (`whatsapp_number`);

--
-- Constraints for table `weekly_event_summary`
--
ALTER TABLE `weekly_event_summary`
  ADD CONSTRAINT `weekly_event_summary_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
