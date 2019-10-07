
package moran.space;

import jam.app.JamProperties;
import jam.bravais.Lattice;
import jam.lang.ObjectFactory;
import jam.math.IntUtil;
import jam.util.RegexUtil;

import moran.cell.Cell;

final class GlobalSpace {
    private final String strProp;
    private final ObjectFactory<? extends Cell> factory;

    private GlobalSpace(ObjectFactory<? extends Cell> factory) {
        this.factory = factory;
        this.strProp = JamProperties.getRequired(Space.STRUCTURE_PROPERTY);
    }

    static Space create(ObjectFactory<? extends Cell> factory) {
        return new GlobalSpace(factory).create();
    }

    private Space create() {
        if (isPointStructure())
            return parsePointStructure();
        else
            return parseLatticeStructure();
    }

    private boolean isPointStructure() {
        return strProp.startsWith("POINT");
    }

    private Space parsePointStructure() {
        String[] fields = RegexUtil.split(RegexUtil.SEMICOLON, strProp, 2);

        if (!fields[0].equals("POINT"))
            throw new IllegalStateException("Invalid point structure specification.");

        int size = IntUtil.parseInt(fields[1]);
        return Space.point(factory, size);
    }

    private Space parseLatticeStructure() {
        Lattice<Cell> lattice = Lattice.parse(strProp);
        lattice.fill(factory);

        return Space.lattice(lattice);
    }
}
