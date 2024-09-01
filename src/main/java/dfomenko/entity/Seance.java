package dfomenko.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import dfomenko.utils.TimeUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.temporal.ChronoField.MILLI_OF_DAY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seance")
public class Seance {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // For default not null value
    @Column(name = "film_id")
    private Long filmId = -1L;

    // For default not null value
    @Column(name = "begin_time")
    private Timestamp beginTime = Timestamp.valueOf(LocalDateTime.now());

    @Column(name = "base_price")
    private Double basePrice;

    @Column(name = "bill_to_pay_id")
    private Long billToPayId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false, insertable = false, updatable = false)
    private Film film;


    @Formula("(select count(i.id) from ticket i where i.seance_id = id and i.place_status = 'FREE')")
    private Long freeTicketsCount;

    public void setSeanceIdString(String newSeanceIdString) {
        if (newSeanceIdString != null) {
            this.id = Long.parseLong(newSeanceIdString);
        }
    }

    public Boolean isActualBeginTime() {
        TimeUtils timeUtils = new TimeUtils();
        return beginTime.after(timeUtils.getTimeNow());
    }

    public Timestamp calcEndTime(LocalTime filmDuration) {
        return new Timestamp(beginTime.getTime() + filmDuration.getLong(MILLI_OF_DAY));
    }


    public String getBeginTimeString() {
        TimeUtils timeUtils = new TimeUtils();
        return timeUtils.getTimeString(beginTime);
    }


    public void setBeginTimeString(String newTimeString) throws ParseException {
        TimeUtils timeUtils = new TimeUtils();
        beginTime = timeUtils.getTimestampFromTimeString(newTimeString, beginTime);
    }

    public String getBeginDateString() {
        TimeUtils timeUtils = new TimeUtils();
        return timeUtils.getDateString(beginTime);
    }


    public void setBeginDateString(String newDateString) throws ParseException {
        TimeUtils timeUtils = new TimeUtils();
        beginTime = timeUtils.getTimestampFromDateString(newDateString, beginTime);
    }
}
