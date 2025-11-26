FROM gradle:8.5.0-jdk21 as build
WORKDIR /app
COPY . .

# ENV URL=$URL

RUN gradle build
RUN ls -la /app

FROM amazoncorretto:21
COPY --from=build /app/build/libs/*.jar /
EXPOSE 8081
CMD java -XX:+UseG1GC -Duser.timezone=UTC -jar $(ls | grep .jar | grep -v plain)
