/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.dao;

import org.dspace.core.Context;
import org.dspace.content.Bitstream;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRowIterator;
import org.dspace.storage.rdbms.TableRow;

import java.sql.SQLException;
import org.dspace.content.MetadataField;
import org.dspace.content.MetadataSchema;
import org.dspace.core.Constants;

public class ItemDAOOracle extends ItemDAO
{		
		private static final String selectPrimaryBitstreamID =
            "SELECT bundle.primary_bitstream_id FROM item2bundle, bundle " +
            "WHERE item2bundle.item_id=? AND item2bundle.bundle_id=bundle.bundle_id AND bundle.name=?";

    private static final String selectFirstBitstreamID =
        "SELECT bundle2bitstream.bitstream_id FROM item2bundle, bundle, bundle2bitstream " +
        "WHERE item2bundle.item_id=? AND item2bundle.bundle_id=bundle.bundle_id AND bundle.name=? " +
        "AND bundle.bundle_id=bundle2bitstream.bundle_id";
    
		private static final String selectNamedBitstreamID =
        "SELECT bitstream.bitstream_id FROM item2bundle, bundle, bundle2bitstream, bitstream " +
        "WHERE item2bundle.item_id=? AND item2bundle.bundle_id=bundle.bundle_id AND bundle.name=? " +
        "AND bundle.bundle_id=bundle2bitstream.bundle_id AND bundle2bitstream.bitstream_id=bitstream.bitstream_id " +
        "AND bitstream.name=?";
    
    private final String SELECT_PRIMARY_BITSTREAM_ID;

    private final String SELECT_FIRST_BITSTREAM_ID;

    private final String SELECT_NAMED_BITSTREAM_ID;

    ItemDAOOracle(Context ctx)
    {
        super(ctx);

        int MD_FIELD_ID_NAME = -1;
        try {
            MD_FIELD_ID_NAME = MetadataField.findByElement(ctx,
                    MetadataSchema.DC_SCHEMA_ID, "title", null).getFieldID();
        } catch (SQLException ex) { /* SNH */ }

        SELECT_PRIMARY_BITSTREAM_ID =
            "SELECT bundle.primary_bitstream_id"
                + " FROM item2bundle"
                + "  JOIN bundle USING (bundle_id)"
                + "  JOIN metadatavalue MD1 ON ("
                + "   MD1.resource_type_id = " +  Constants.BUNDLE
                + "   AND MD1.resource_id = bundle_id"
                + "   AND MD1.metadata_field_id = " + MD_FIELD_ID_NAME
                + "  )"
                + " WHERE item2bundle.item_id = ?"
                + "  AND dbms_lob.compare(MD1.text_value, ?) = 0";

        SELECT_FIRST_BITSTREAM_ID =
            "SELECT bundle2bitstream.bitstream_id"
                + " FROM item2bundle"
                + "  JOIN bundle USING (bundle_id)"
                + "  JOIN bundle2bitstream USING (bundle_id)"
                + "  JOIN metadatavalue MD1 ON ("
                + "   MD1.resource_type_id = " +  Constants.BUNDLE
                + "   AND MD1.resource_id = bundle_id"
                + "   AND MD1.metadata_field_id = " + MD_FIELD_ID_NAME
                + "  )"
                + " WHERE item2bundle.item_id = ?"
                + "  AND dbms_lob.compare(MD1.text_value, ?) = 0";

        SELECT_NAMED_BITSTREAM_ID =
            "SELECT bitstream_id"
                + " FROM item2bundle"
                + "  JOIN bundle USING (bundle_id)"
                + "  JOIN bundle2bitstream USING (bundle_id)"
                + "  JOIN bitstream USING (bitstream_id)"
                + "  JOIN metadatavalue MD1 ON ("
                + "   MD1.resource_type_id = " +  Constants.BUNDLE
                + "   AND MD1.resource_id = bundle_id"
                + "   AND MD1.metadata_field_id = " + MD_FIELD_ID_NAME
                + "  )"
                + "  JOIN metadatavalue MD2 ON ("
                + "   MD2.resource_type_id = " +  Constants.BITSTREAM
                + "   AND MD2.resource_id = bitstream_id"
                + "   AND MD2.metadata_field_id = " + MD_FIELD_ID_NAME
                + "  )"
                + " WHERE item2bundle.item_id = ?"
                + "  AND dbms_lob.compare(MD1.text_value, ?) = 0 "
                + "  AND dbms_lob.compare(MD2.text_value, ?) = 0";
    }

    @Override
    public Bitstream getPrimaryBitstream(int itemId, String bundleName) throws SQLException
    {
        TableRowIterator tri = null;

        try
        {
            tri = DatabaseManager.query(context, SELECT_PRIMARY_BITSTREAM_ID, itemId, bundleName);

            if (tri.hasNext())
            {
                TableRow row = tri.next();
                int bid = row.getIntColumn("primary_bitstream_id");
                return Bitstream.find(context, bid);
            }
        }        
        finally
        {
            if (tri != null)
            {
                tri.close();
            }
        }

        return null;
    }

    @Override
    public Bitstream getFirstBitstream(int itemId, String bundleName) throws SQLException
    {
        TableRowIterator tri = null;

        try
        {
            tri = DatabaseManager.query(context, SELECT_FIRST_BITSTREAM_ID, itemId, bundleName);
            if (tri.hasNext())
            {
                TableRow row = tri.next();
                int bid = row.getIntColumn("bitstream_id");
                return Bitstream.find(context, bid);
            }
        }
        finally
        {
            if (tri != null)
            {
                tri.close();
            }
        }

        return null;
    }

    @Override
    public Bitstream getNamedBitstream(int itemId, String bundleName, String fileName) throws SQLException
    {
        TableRowIterator tri = null;

        try
        {
            tri = DatabaseManager.query(context, SELECT_NAMED_BITSTREAM_ID, itemId, bundleName, fileName);
            if (tri.hasNext())
            {
                TableRow row = tri.next();
                int bid = row.getIntColumn("bitstream_id");
                return Bitstream.find(context, bid);
            }
        }
        finally
        {
            if (tri != null)
            {
                tri.close();
            }
        }

        return null;
    }
}
