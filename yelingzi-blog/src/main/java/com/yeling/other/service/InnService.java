package com.yeling.other.service;

import com.yeling.other.domian.entity.Inn;
import com.yeling.other.domian.entity.InnLedger;

import java.util.List;
import java.util.Map;

public interface InnService {

    List<Inn> getInn();

    Map<String, Object> submitInn(InnLedger innLedger, Integer uid);

    List<InnLedger> getInnRoom();

    Boolean addInnRoom(InnLedger innLedger);

    void delInnRoom(Integer id);

    void innCountCorrect();

    Boolean addInnRoomClassify(Inn inn);

    void delInn(Integer id);

}
