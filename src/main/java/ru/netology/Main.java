package ru.netology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Leonid Zulin
 * @date 07.01.2023 14:23
 */
public class Main {

    // наш адрес с сгенерированным ключом
    public static String URL = "https://api.nasa.gov/planetary/apod?api_key=YNyrOW29W5bG8B7WkkaQqbzby3EsR2pia8euwROP";

    // переводит строку в объект JAVA
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        // Настраиваем HTTP клиент, для запросов и ответов
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        // Отправляем запрос и получаем ответ
        HttpGet request = new HttpGet(URL);
        CloseableHttpResponse response = httpClient.execute(request);

        // Наш ответ преобразуем в объект JAVA
        NasaFile nasaFile = mapper.readValue(response.getEntity().getContent(), NasaFile.class);
        System.out.println(nasaFile);

        // Опять отправляем запрос и получаем ответ с картинкой
        HttpGet pictureRequest = new HttpGet(nasaFile.getUrl());
        CloseableHttpResponse pictureResponse = httpClient.execute(pictureRequest);

        // Формирование автоматического названия файла
        String[] array = nasaFile.getUrl().split("/"); // разделить по символу '/'
        String fileName = array[array.length - 1];

        // проверка на null и сохранение в файл
        HttpEntity entity = pictureResponse.getEntity();
        if (entity != null) {
            FileOutputStream fos = new FileOutputStream(fileName);
            entity.writeTo(fos);
            fos.close();// закрыть наш стрим
        } else {
            System.out.println("Object not found!!!");
        }
    }
}
