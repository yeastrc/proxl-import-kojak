package org.yeastrc.proxl.xml.kojak.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinkableEnds {

    public LinkableEnds( LinkableEnd end1, LinkableEnd end2 ) {

        ArrayList<LinkableEnd> tmpList = new ArrayList<>(2 );
        tmpList.add( end1 );
        tmpList.add( end2 );

        this.linkableEnds = Collections.unmodifiableList( tmpList );
    }

    private List linkableEnds;

}
