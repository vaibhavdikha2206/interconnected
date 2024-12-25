-- Create the Experts table
CREATE TABLE Experts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    organization VARCHAR(100) NOT NULL,
    thumbnail VARCHAR(100),
    other_details TEXT
);

-- Create the TimeSlots table
-- we can remove end_time later on
CREATE TABLE TimeSlots (
    id SERIAL PRIMARY KEY,
    day_of_week VARCHAR(9) CHECK (day_of_week IN ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday')),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT check_start_end_time CHECK (start_time < end_time)
);

-- Create the Services table
CREATE TABLE Services (
    id SERIAL PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    duration INT NOT NULL,  -- Duration in minutes
    price DECIMAL(10, 2) NOT NULL
);

-- Create the Skills table
CREATE TABLE Skills (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create the Roles table
CREATE TABLE Roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create the Users table
CREATE TABLE Users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    locale VARCHAR(10),
    picture_url VARCHAR(2083),
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create the Orders table
CREATE TABLE Orders (
    id SERIAL PRIMARY KEY,
    razorpay_order_id VARCHAR(255),
    razorpay_payment_id VARCHAR(255),
    customer_email VARCHAR(255) NOT NULL,
    cost DOUBLE PRECISION NOT NULL,
    currency VARCHAR(10) NOT NULL,
    order_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(255) NOT NULL,
    order_status VARCHAR(255) NOT NULL,
    service_id BIGINT NOT NULL,
    service_timeslot TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expert_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (service_id) REFERENCES Services(id),
    FOREIGN KEY (expert_id) REFERENCES Experts(id)
);

-- Create the ExpertServices table
CREATE TABLE Expert_Service (
    id SERIAL PRIMARY KEY,
    expert_id INT NOT NULL,
    service_id INT NOT NULL,
    FOREIGN KEY (expert_id) REFERENCES Experts(id),
    FOREIGN KEY (service_id) REFERENCES Services(id)
);

-- Create the ExpertTimeSlots table
CREATE TABLE Expert_TimeSlot (
    id SERIAL PRIMARY KEY,
    expert_id INT NOT NULL,
    timeslot_id INT NOT NULL,
    FOREIGN KEY (expert_id) REFERENCES Experts(id),
    FOREIGN KEY (timeslot_id) REFERENCES TimeSlots(id)
);

-- Create the UserRoles table
CREATE TABLE User_Roles (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (role_id) REFERENCES Roles(id),
    CONSTRAINT unique_user_role UNIQUE (user_id, role_id)
);

-- Create the ExpertSkill table
CREATE TABLE Expert_Skill (
    expert_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (expert_id, skill_id),
    FOREIGN KEY (expert_id) REFERENCES Experts(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES Skills(id) ON DELETE CASCADE
);

-- Add indexes for foreign keys
CREATE INDEX idx_orders_service_id ON Orders(service_id);
CREATE INDEX idx_orders_expert_id ON Orders(expert_id);
CREATE INDEX idx_expert_service_expert_id ON Expert_Service(expert_id);
CREATE INDEX idx_expert_service_service_id ON Expert_Service(service_id);
CREATE INDEX idx_expert_timeslot_expert_id ON Expert_TimeSlot(expert_id);
CREATE INDEX idx_expert_timeslot_timeslot_id ON Expert_TimeSlot(timeslot_id);
CREATE INDEX idx_user_roles_user_id ON User_Roles(user_id);
CREATE INDEX idx_user_roles_role_id ON User_Roles(role_id);
