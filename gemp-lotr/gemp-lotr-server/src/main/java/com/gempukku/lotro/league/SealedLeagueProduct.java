package com.gempukku.lotro.league;

import com.gempukku.lotro.game.CardCollection;
import com.gempukku.lotro.game.DefaultCardCollection;
import com.gempukku.lotro.game.MutableCardCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SealedLeagueProduct {
    private Map<String, List<CardCollection>> _collections = new HashMap<String, List<CardCollection>>();

    public SealedLeagueProduct() {
        createFellowshipBlock();
        createTowersBlock();
        createMovieBlock();
        createWarOfTheRingBlock();
        createMovieSpecialBlock();
    }

    private void createFellowshipBlock() {
        List<CardCollection> fotrBlock = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection();
        firstWeek.addItem("(S)FotR - Starter", 1);
        firstWeek.addItem("FotR - Booster", 6);
        firstWeek.addItem("1_231", 2);
        fotrBlock.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection();
        secondWeek.addItem("(S)MoM - Starter", 1);
        secondWeek.addItem("MoM - Booster", 3);
        secondWeek.addItem("2_51", 1);
        fotrBlock.add(secondWeek);

        MutableCardCollection thirdWeek = new DefaultCardCollection();
        thirdWeek.addItem("(S)RotEL - Starter", 1);
        thirdWeek.addItem("RotEL - Booster", 3);
        fotrBlock.add(thirdWeek);

        MutableCardCollection fourthWeek = new DefaultCardCollection();
        fourthWeek.addItem("FotR - Booster", 2);
        fourthWeek.addItem("MoM - Booster", 2);
        fourthWeek.addItem("RotEL - Booster", 2);
        fotrBlock.add(fourthWeek);

        _collections.put(SealedLeagueType.FOTR_BLOCK.getSealedCode(), fotrBlock);
    }

    private void createTowersBlock() {
        List<CardCollection> tttBlock = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection();
        firstWeek.addItem("(S)TTT - Starter", 1);
        firstWeek.addItem("TTT - Booster", 6);
        firstWeek.addItem("4_356", 1);
        firstWeek.addItem("4_249", 2);
        tttBlock.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection();
        secondWeek.addItem("(S)BoHD - Starter", 1);
        secondWeek.addItem("BoHD - Booster", 3);
        tttBlock.add(secondWeek);

        MutableCardCollection thirdWeek = new DefaultCardCollection();
        thirdWeek.addItem("(S)EoF - Starter", 1);
        thirdWeek.addItem("EoF - Booster", 3);
        tttBlock.add(thirdWeek);

        MutableCardCollection fourthWeek = new DefaultCardCollection();
        fourthWeek.addItem("TTT - Booster", 2);
        fourthWeek.addItem("BoHD - Booster", 2);
        fourthWeek.addItem("EoF - Booster", 2);
        tttBlock.add(fourthWeek);

        _collections.put(SealedLeagueType.TTT_BLOCK.getSealedCode(), tttBlock);
    }

    private void createMovieBlock() {
        List<CardCollection> kingBlock = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection();
        firstWeek.addItem("(S)RotK - Starter", 1);
        firstWeek.addItem("RotK - Booster", 6);
        firstWeek.addItem("7_212", 1);
        firstWeek.addItem("1_231", 1);
        firstWeek.addItem("7_355", 1);
        firstWeek.addItem("7_358", 1);
        kingBlock.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection();
        secondWeek.addItem("(S)SoG - Starter", 1);
        secondWeek.addItem("SoG - Booster", 3);
        secondWeek.addItem("8_120", 1);
        kingBlock.add(secondWeek);

        MutableCardCollection thirdWeek = new DefaultCardCollection();
        thirdWeek.addItem("(S)MD - Starter", 1);
        thirdWeek.addItem("MD - Booster", 3);
        thirdWeek.addItem("7_326", 2);
        kingBlock.add(thirdWeek);

        MutableCardCollection fourthWeek = new DefaultCardCollection();
        fourthWeek.addItem("RotK - Booster", 2);
        fourthWeek.addItem("SoG - Booster", 2);
        fourthWeek.addItem("MD - Booster", 2);
        kingBlock.add(fourthWeek);

        _collections.put(SealedLeagueType.MOVIE_BLOCK.getSealedCode(), kingBlock);
    }

    private void createMovieSpecialBlock() {
        List<CardCollection> movieSpecialBlock = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection();
        firstWeek.addItem("(S)Special-1-3", 1);
        firstWeek.addItem("FotR - Booster", 1);
        firstWeek.addItem("TTT - Booster", 1);
        firstWeek.addItem("RotK - Booster", 1);
        firstWeek.addItem("(S)Movie Booster Choice", 3);
        movieSpecialBlock.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection();
        secondWeek.addItem("(S)Special-4-6", 1);
        secondWeek.addItem("MoM - Booster", 1);
        secondWeek.addItem("BoHD - Booster", 1);
        secondWeek.addItem("SoG - Booster", 1);
        movieSpecialBlock.add(secondWeek);

        MutableCardCollection thirdWeek = new DefaultCardCollection();
        thirdWeek.addItem("(S)Special-7-9", 1);
        thirdWeek.addItem("RotEL - Booster", 1);
        thirdWeek.addItem("EoF - Booster", 1);
        thirdWeek.addItem("MD - Booster", 1);
        movieSpecialBlock.add(thirdWeek);

        MutableCardCollection fourthWeek = new DefaultCardCollection();
        fourthWeek.addItem("REF - Booster", 1);
        fourthWeek.addItem("(S)Movie Booster Choice", 5);
        movieSpecialBlock.add(fourthWeek);

        _collections.put(SealedLeagueType.MOVIE_SPECIAL_BLOCK.getSealedCode(), movieSpecialBlock);
    }

    private void createWarOfTheRingBlock() {
        List<CardCollection> warOfTheRingBlock = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection();
        firstWeek.addItem("(S)SH - Starter", 1);
        firstWeek.addItem("SH - Booster", 6);
        firstWeek.addItem("12_145", 1);
        firstWeek.addItem("12_181", 1);
        firstWeek.addItem("12_88", 1);
        firstWeek.addItem("12_70", 1);
        warOfTheRingBlock.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection();
        secondWeek.addItem("(S)BR - Starter", 1);
        secondWeek.addItem("BR - Booster", 3);
        warOfTheRingBlock.add(secondWeek);

        MutableCardCollection thirdWeek = new DefaultCardCollection();
        thirdWeek.addItem("(S)BL - Starter", 1);
        thirdWeek.addItem("BL - Booster", 3);
        warOfTheRingBlock.add(thirdWeek);

        MutableCardCollection fourthWeek = new DefaultCardCollection();
        fourthWeek.addItem("SH - Booster", 2);
        fourthWeek.addItem("BR - Booster", 2);
        fourthWeek.addItem("BL - Booster", 2);
        warOfTheRingBlock.add(fourthWeek);

        _collections.put(SealedLeagueType.WAR_BLOCK.getSealedCode(), warOfTheRingBlock);
    }

    public CardCollection getCollectionForSerie(String leagueCode, int serieIndex) {
        return _collections.get(leagueCode).get(serieIndex);
    }
}
