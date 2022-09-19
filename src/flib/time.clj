(ns flib.time
  (:import [java.time LocalDate LocalDateTime Duration ZonedDateTime]
           [java.time.format DateTimeFormatter]
           [java.time.temporal ChronoUnit]))

(defn unix-time
  ([] (unix-time (ZonedDateTime/now)))
  ([zoned-date-time] (.toEpochSecond zoned-date-time)))

(defn unix-time-plus-minutes
  [minutes]
  (unix-time (.plus (ZonedDateTime/now)
                    minutes ChronoUnit/MINUTES)))
