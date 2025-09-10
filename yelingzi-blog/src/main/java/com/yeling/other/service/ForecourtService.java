package com.yeling.other.service;

import com.yeling.other.domian.entity.Verse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface ForecourtService {

    String getIpAddr(HttpServletRequest request, HttpServletResponse response);

    Verse fundVerse(String string);

    void addVerse(Verse verse);

    List<Verse> showVerse();

    List<Verse> getIpVerse(String ip);

}
