package com.github.vvpanf.campusapi.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.yml")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CampusApiApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	private String apiV1(String url) {
		return "/api/v1/" + url;
	}

	@Test
	void handleCampuses_GetRequest_ReturnAllCampusesInJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/campuses")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"content\":[{\"id\":1,\"name\":\"POSIX\",\"address\":\"666605, Ивановская область, город Зарайск, пр. Бухарестская, 78\",\"parkingSpaces\":100},{\"id\":2,\"name\":\"INTEX\",\"address\":\"385509, Кировская область, город Люберцы, проезд Космонавтов, 12\",\"parkingSpaces\":50}],\"pageable\":{\"pageNumber\":0,\"pageSize\":10,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"unpaged\":false,\"paged\":true},\"last\":true,\"totalElements\":2,\"totalPages\":1,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"first\":true,\"numberOfElements\":2,\"empty\":false}"));
	}

	@Test
	void handleCampuses_GetRequestWithPageAndCount_ReturnCampusesInJson() throws Exception {
		mockMvc
				.perform(get(apiV1("/campuses")).param("page", "1").param("count", "1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json("{\"content\":[{\"id\":2,\"name\":\"INTEX\",\"address\":\"385509, Кировская область, город Люберцы, проезд Космонавтов, 12\",\"parkingSpaces\":50}],\"pageable\":{\"pageNumber\":1,\"pageSize\":1,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":1,\"paged\":true,\"unpaged\":false},\"last\":true,\"totalElements\":2,\"totalPages\":2,\"first\":false,\"size\":1,\"number\":1,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":1,\"empty\":false}"));
	}

	@Test
	void handleCampuses_GetByNonExistId_ReturnNotFound() throws Exception {
		mockMvc
			.perform(get(apiV1("/campuses/0")))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	void handleCampuses_GetById_ReturnCampusInJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/campuses/1")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"id\":1,\"name\":\"POSIX\",\"address\":\"666605, Ивановская область, город Зарайск, пр. Бухарестская, 78\",\"parkingSpaces\":100}"));
	}

	@Test
	void handleCampuses_GetByName_ReturnCampusNamedPosixInJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/campuses")).param("name", "POSIX"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"id\":1,\"name\":\"POSIX\",\"address\":\"666605, Ивановская область, город Зарайск, пр. Бухарестская, 78\",\"parkingSpaces\":100}"));
	}

	@Test
	void handleCampuses_AddNewCampusWithWrongParam_ReturnBadRequest() throws Exception {
		mockMvc
			.perform(post(apiV1("/campuses"))
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
			{
				"qwe":"CAMPUS",
				"address":"123456, Московская область, город Зарайск, пр. Бухарестская, 78,",
				"parkingSpaces":10
			}
			"""))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().json("{\"errors\":[\"Campus name is required\"]}"));
	}

	@Test
	void handleCampuses_AddNewCampus_ReturnNewCampusJsonWithId() throws Exception {
		mockMvc
			.perform(post(apiV1("/campuses"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
				{
					"name": "CAMPUS",
					"address": "123456, Московская область, город Зарайск, пр. Бухарестская, 78,",
					"parkingSpaces": 10
				}
				"""))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(content().json("{\"id\":3,\"name\":\"CAMPUS\",\"address\":\"123456, Московская область, город Зарайск, пр. Бухарестская, 78,\",\"parkingSpaces\":10}"));
	}

	@Test
	void handleRooms_GetAllRoomsForCampusPosix_ReturnAllRoomsInJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/campuses/1/rooms")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"content\":[" +
					"{\"id\":1,\"number\":\"101\",\"type\":\"Лабораторная\",\"capacity\":15}," +
					"{\"id\":4,\"number\":\"101\",\"type\":\"Лекционная\",\"capacity\":40}," +
					"{\"id\":5,\"number\":\"520-a\",\"type\":\"Лабораторная\",\"capacity\":10}]," +
					"\"pageable\":{\"pageNumber\":0,\"pageSize\":10,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"unpaged\":false,\"paged\":true},\"last\":true,\"totalElements\":3,\"totalPages\":1,\"first\":true,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":3,\"empty\":false}"));
	}

	@Test
	void handleRooms_GetAllRoomsForCampusPosixWithMinNumberOfSeats_ReturnRoomsByConditionInJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/campuses/1/rooms")).param("minNumberOfSeats", "15"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"content\":[" +
					"{\"id\":1,\"number\":\"101\",\"type\":\"Лабораторная\",\"capacity\":15}," +
					"{\"id\":4,\"number\":\"101\",\"type\":\"Лекционная\",\"capacity\":40}]," +
					"\"pageable\":{\"pageNumber\":0,\"pageSize\":10,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"unpaged\":false,\"paged\":true},\"last\":true,\"totalElements\":2,\"totalPages\":1,\"first\":true,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":2,\"empty\":false}"));
	}

	@Test
	void handleRooms_GetRoomByNonExistId_ReturnIsNotFound() throws Exception {
		mockMvc
			.perform(get(apiV1("/campuses/1/rooms/2")))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	void handleRooms_GetRoomById_ReturnRoomInJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/campuses/1/rooms/1")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"id\":1,\"number\":\"101\",\"type\":\"Лабораторная\",\"capacity\":15}"));
	}

	@Test
	void handleRooms_AddNewRoom_ReturnNewRoomJsonWithId() throws Exception {
		mockMvc
			.perform(post(apiV1("/campuses/1/rooms"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
    			{
					"number": "505",
					"type": "Лабораторная",
					"capacity": 30
				}
				"""))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(content().json("{\"id\":6,\"number\":\"505\",\"type\":\"Лабораторная\",\"capacity\":30}"));
	}

	@Test
	void handleReservations_GetRoomReservationsByNonExistId_ReturnEmptyArray() throws Exception {
		mockMvc
			.perform(get(apiV1("/campuses/1/rooms/0/reservations")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"content\":[],\"pageable\":\"INSTANCE\",\"totalElements\":0,\"totalPages\":1,\"last\":true,\"size\":0,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":0,\"first\":true,\"empty\":true}"));
	}

	@Test
	void handleReservations_GetRoomReservationsById_ReturnReservationsJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/campuses/1/rooms/1/reservations")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"content\":[" +
				"{\"id\":1,\"dateOfReserv\":\"2024-03-01\",\"startTime\":\"12:00:00\",\"endTime\":\"15:30:00\",\"comment\":\"\"}," +
				"{\"id\":3,\"dateOfReserv\":\"2024-03-02\",\"startTime\":\"18:00:00\",\"endTime\":\"19:00:00\",\"comment\":\"\"}]," +
				"\"pageable\":\"INSTANCE\",\"last\":true,\"totalPages\":1,\"totalElements\":2,\"first\":true,\"size\":2,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":2,\"empty\":false}"));
	}

	@Test
	void handleUsers_GetAllUsers_ReturnAllUsersInJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/users")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"content\":[" +
				  "{\"id\":1,\"fullName\":\"USER 1\",\"dateOfBirth\":\"2000-01-01\",\"email\":\"user@user.u\"}," +
				  "{\"id\":2,\"fullName\":\"USER 2\",\"dateOfBirth\":\"1990-12-01\",\"email\":\"qwe@user.u\"}]," +
				  "\"pageable\":{\"pageNumber\":0,\"pageSize\":10,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"unpaged\":false,\"paged\":true},\"last\":true,\"totalElements\":2,\"totalPages\":1,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"first\":true,\"numberOfElements\":2,\"empty\":false}"));
	}

	@Test
	void handleUsers_GetUsersByNameMatches_ReturnUsersByConditionInJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/users")).param("nameMatches", "1"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"content\":[" +
				  "{\"id\":1,\"fullName\":\"USER 1\",\"dateOfBirth\":\"2000-01-01\",\"email\":\"user@user.u\"}]," +
				  "\"pageable\":{\"pageNumber\":0,\"pageSize\":10,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"unpaged\":false,\"paged\":true},\"last\":true,\"totalElements\":1,\"totalPages\":1,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"first\":true,\"numberOfElements\":1,\"empty\":false}"));
	}

	@Test
	void handleUsers_GetUsersByNonExistId_ReturnNotFound() throws Exception {
		mockMvc
			.perform(get(apiV1("/users/0")))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	void handleUsers_GetUsersById_ReturnUserInJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/users/1")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"id\":1,\"fullName\":\"USER 1\",\"dateOfBirth\":\"2000-01-01\",\"email\":\"user@user.u\"}"));
	}

	@Test
	void handleUsers_AddNewUser_ReturnNewUserJsonWithId() throws Exception {
		mockMvc
			.perform(post(apiV1("/users"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
				{
					"fullName": "USER 3",
					"dateOfBirth": "2010-10-10",
					"email": "third-user@mail.u"
				}
				"""))
			.andExpect(status().isCreated())
			.andExpect(content().json("{\"id\":3,\"fullName\":\"USER 3\",\"dateOfBirth\":\"2010-10-10\",\"email\":\"third-user@mail.u\"}"));
	}

	@Test
	void handleReservations_GetReservationsByUser_ReturnReservationsInJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/users/1/reservations")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"content\":[" +
				  "{\"id\":1,\"dateOfReserv\":\"2024-03-01\",\"startTime\":\"12:00:00\",\"endTime\":\"15:30:00\",\"comment\":\"\"}," +
				  "{\"id\":3,\"dateOfReserv\":\"2024-03-02\",\"startTime\":\"18:00:00\",\"endTime\":\"19:00:00\",\"comment\":\"\"}]," +
				  "\"pageable\":{\"pageNumber\":0,\"pageSize\":10,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"paged\":true,\"unpaged\":false},\"last\":true,\"totalPages\":1,\"totalElements\":2,\"first\":true,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":2,\"empty\":false}"));
	}

	@Test
	void handleReservations_GetReservationsByNonExistId_ReturnNotFound() throws Exception {
		mockMvc
			.perform(get(apiV1("/users/1/reservations/0")))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	void handleReservations_GetReservationsById_ReturnReservationInJson() throws Exception {
		mockMvc
			.perform(get(apiV1("/users/1/reservations/1")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("{\"id\":1,\"dateOfReserv\":\"2024-03-01\",\"startTime\":\"12:00:00\",\"endTime\":\"15:30:00\",\"comment\":\"\"}"));
	}

	@Test
	void handleReservation_AddNonExistRoomToReservation_ReturnBadRequest() throws Exception {
		mockMvc
			.perform(put(apiV1("/users/1/reservations/1/rooms/0")))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	void handleReservation_AddRoomToReservation_ReturnCreatedStatus() throws Exception {
		mockMvc
			.perform(put(apiV1("/users/1/reservations/1/rooms/5")))
			.andDo(print())
			.andExpect(status().isCreated());
	}
}
