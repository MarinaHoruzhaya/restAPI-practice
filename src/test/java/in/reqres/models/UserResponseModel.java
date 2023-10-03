package in.reqres.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseModel {
    String name, job;
    Integer id;
    LocalDateTime time;

}
