/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     thibaud
 */
package org.nuxeo.km.operations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.local.LocalSession;
import org.nuxeo.ecm.core.model.Document;
import org.nuxeo.ecm.core.model.Session;
import org.nuxeo.ecm.platform.dublincore.listener.DublinCoreListener;
import org.nuxeo.labs.rating.model.RatingImpl;
import org.nuxeo.labs.rating.service.RatingService;
import org.nuxeo.runtime.transaction.TransactionHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Must be run from a container where the articles will be created.
 */
@Operation(id = CreateDemoDataOp.ID, category = Constants.CAT_SERVICES, label = "CVS: Create Data Demo", description = "")
public class CreateDemoDataOp {

    public static final String ID = "CVS.CreateDataDemoOp";

    private static final Log log = LogFactory.getLog(CreateDemoDataOp.class);

    protected static final int COMMIT_MODULO = 25;

    protected static final String[] USERS = { "Administrator", "reviewer1", "reviewer1", "reviewer2", "reviewer3",
            "reviewer3" };

    protected static final int USERS_MAX = USERS.length - 1;

    protected static final String[] USERS_VOTE = { "m1", "ma1", "ma2", "ma2", "ma2", "mw1", "mw2", "mw2", "mw2", "mw2",
            "mw2", "mw2", "mw2", "ne1", "ne2", "ne3", "se1", "se1", "se1", "se1", "se1", "se2", "se3", "reviewer1",
            "reviewer1", "reviewer1", "reviewer1", "reviewer2", "reviewer3" };

    protected static final int USERS_VOTE_MAX = USERS_VOTE.length - 1;

    protected static final String CATEGORY_DRUG_DESC = "Drug Description";

    protected static final String[] REGIONS = { "Northeast", "Northeast", "Northeast", "Midatlantic", "Midatlantic",
            "Southeast", "Midwest", "Midwest", "Mountain", "Mountain", "Mountain", "Mountain", "Pacific" };

    protected static final int REGIONS_MAX = REGIONS.length - 1;

    protected static final int REGIONS_MAX_DIFFERENT = 6;

    protected static final String CVS_URL_PREFIX = "https://www.cvs.com/drug/";

    protected static final String[] DRUG_NAMES = { "Abilify", "Acetaminophen", "Adderall", "Advair", "Albuterol",
            "Alendronic-Acid", "Alprazolam", "Ambien", "Amitriptyline", "Amlodipine", "Amoxicillin", "Aspirin",
            "Atenolol", "Ativan", "Azithromycin-Dihydrate", "Celebrex", "Celexa", "Cephalexin-Monohydrate", "Cialis",
            "Cipro", "Ciprofloxacin", "Citalopram", "Clindamycin", "Clonazepam", "Concerta", "Coumadin", "Crestor",
            "Cyclobenzaprine", "Cymbalta", "Diazepam", "Diclofenac", "Diovan", "Doxycycline-Hyclate", "Effexor",
            "Fluconazole", "Fluoxetine", "Furosemide", "Gabapentin", "Hydrochlorothiazide", "Hydrocortisone",
            "Hydroxyzine-Hydrochloride", "Ibuprofen", "Lantus", "Levothyroxine-Sodium", "Lexapro", "Lipitor",
            "Lisinopril", "Lithium", "Loratadine", "Lorazepam", "Losartan-Potassium", "Lyrica", "Meloxicam",
            "Metformin-Hydrochloride", "Methadone-Hydrochloride", "Methotrexate-Sodium", "Metoprolol", "Metronidazole",
            "Morphine", "Naproxen", "Nexium", "Norco", "Omeprazole", "Oxycontin", "Oxycodone", "Pantoprazole-Sodium",
            "Paroxetine", "Penicillin", "Percocet", "Phentermine-Hydrochloride", "Plavix", "Pradaxa", "Prednisone",
            "Promethazine", "Propecia", "Propranolol-Hydrochloride", "Prozac", "Ranitidine-Hydrochloride", "Ritalin",
            "Seroquel", "Sertraline", "Simvastatin", "Singulair", "Suboxone", "Synthroid", "Tramadol", "Trazodone",
            "Valium", "Ventolin", "Viagra", "Vyvanse", "Warfarin", "Wellbutrin", "Xanax", "Zoloft", "Zolpidem",
            "Zyprexa" };

    protected static final int DRUG_NAMES_MAX = DRUG_NAMES.length - 1;

    protected static final String STATE_APPROVED = "approved";
    
    protected static final String[] GROUPS = {
        "North-East", "North-East", "North-East", "North-East",
        "Mid-Atlantic",
        "South-East", "South-East", "South-East",
        "Midwest", "Midwest", "Midwest", "Midwest", "Midwest",
        "Mountain", "Mountain",
        "Pacific", "Pacific", "Pacific", "Pacific", "Pacific",
    };
    
    protected static final int GROUPS_MAX = GROUPS.length - 1;

    protected long todayAsMS;

    protected int count = 0;

    protected String parentPath;

    protected DateFormat _yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

    protected Calendar _today = Calendar.getInstance();

    @Context
    protected CoreSession session;

    @Context
    protected RatingService ratingService;

    @OperationMethod
    public DocumentModel run(DocumentModel inDoc) throws IOException {

        log.warn("Creating " + DRUG_NAMES.length + " Articles...");

        parentPath = inDoc.getPathAsString();
        todayAsMS = Calendar.getInstance().getTimeInMillis();

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        DocumentModel article;
        for (int i = 0; i < DRUG_NAMES.length; ++i) {

            article = CreateOneArticle(DRUG_NAMES[i]);

            CreateVotes(article);

            if ((i % COMMIT_MODULO) == 0) {
                log.warn("Created: " + (i + 1));
            }

        }

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        log.warn("Creation of " + DRUG_NAMES.length + " Articles: Done");

        return inDoc;

    }

    protected DocumentModel CreateOneArticle(String inTitle) throws IOException {

        String url = CVS_URL_PREFIX + inTitle.toLowerCase();

        String html = getOverviewAsHtml(url);
        String htmltitle = "<h2>Overview Information on " + inTitle + "</h2>";
        html = htmltitle + html;

        String aboutHtml = getAboutAsHtml(url);
        htmltitle = "<h2>About " + inTitle + "</h2>";
        html = html + "<p></p><p></p>" + htmltitle + aboutHtml;

        DocumentModel doc;

        String userModif = USERS[RandomValues.randomInt(0, USERS_MAX)];
        // Using a map so we don't duplicate values
        HashMap<String, String> map = new HashMap<String, String>();
        int anInt = RandomValues.randomInt(1, 4);
        do {
            String p = REGIONS[RandomValues.randomInt(0, REGIONS_MAX)];
            map.put(p, p);
        } while (map.size() < anInt);
        String[] regions = new String[anInt];
        map.keySet().toArray(regions);
        
     // Using a map so we don't duplicate values
        map = new HashMap<String, String>();
        anInt = RandomValues.randomInt(1, 2);
        do {
            String p = GROUPS[RandomValues.randomInt(0, GROUPS_MAX)];
            map.put(p, p);
        } while (map.size() < anInt);
        String[] readGroups = new String[anInt];
        map.keySet().toArray(readGroups);

        // Create the model
        doc = session.createDocumentModel(parentPath, inTitle, "Article");

        // Setup values
        doc.setPropertyValue("dc:title", inTitle);
        doc.setPropertyValue("article:category", CATEGORY_DRUG_DESC);
        doc.setPropertyValue("article:region", regions);
        doc.setPropertyValue("note:mime_type", "text/html");
        doc.setPropertyValue("note:note", html);

        //doc.setPropertyValue("readConfirm:assigned", true);
        doc.setPropertyValue("readConfirm:groupnames", readGroups);
        

        // =========================================== dublincore
        doc.setPropertyValue("dc:creator", USERS[RandomValues.randomInt(0, USERS_MAX)]);
        doc.setPropertyValue("dc:lastContributor", userModif);
        Calendar created = RandomValues.buildDate(null, 0, 90, true);
        doc.setPropertyValue("dc:created", created);
        doc.setPropertyValue("dc:modified", created);
        // We don't setup contributors list (no time)
        //@{CurrentDate.months(6).calendar}
        doc.setPropertyValue("dc:expired", RandomValues.addDays(created, 180));

        // Disable dublincore
        doc.putContextData(DublinCoreListener.DISABLE_DUBLINCORE_LISTENER, true);
        doc = session.createDocument(doc);
        //doc.putContextData(DublinCoreListener.DISABLE_DUBLINCORE_LISTENER, true);
        doc = session.saveDocument(doc);

        directSetCurrentLifecycleState(session, doc, "approved");

        return doc;

    }

    protected String getOverviewAsHtml(String inUrl) throws IOException {

        String html = "";
        
        inUrl += "/about-drug";
        URL theUrl = new URL(inUrl);
        URLConnection yc = theUrl.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            html += inputLine + "\n";
        }
        in.close();

        // Now extract only our values
        // This is not state of the art. Should use RegEx etc.
        // We looked at a typical page and saw our content was here
        int pos = html.indexOf("<div class=\"drug-content\">");
        if (pos > -1) {
            html = html.substring(pos);
            int pos2 = html.indexOf("</div>");
            // Assume it is ok
            html = html.substring(0, pos2 + "</div>".length());
        } else {
            html = "";
        }

        return html;
    }

    protected String getAboutAsHtml(String inUrl) throws IOException {

        String html = "";

        URL theUrl = new URL(inUrl);
        URLConnection yc = theUrl.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            html += inputLine + "\n";
        }
        in.close();

        // Now extract only our values
        // This is not state of the art. Should use RegEx etc.
        // We looked at a typical page and saw our content was here
        int pos = html.indexOf("<div class=\"drug-content\">");
        if (pos > -1) {
            html = html.substring(pos);
            int pos2 = html.indexOf("</div>");
            // Assume it is ok
            html = html.substring(0, pos2 + "</div>".length());
        } else {
            html = "";
        }

        return html;
    }

    protected void CreateVotes(DocumentModel inArticle) {

        HashMap<String, Integer> usersAndVotes = new HashMap<String, Integer>();
        int anInt = RandomValues.randomInt(1, 5);
        do {
            String p = USERS_VOTE[RandomValues.randomInt(0, USERS_VOTE_MAX)];
            usersAndVotes.put(p, RandomValues.randomInt(1, 5));
        } while (usersAndVotes.size() < anInt);

        for (Map.Entry<String, Integer> entry : usersAndVotes.entrySet()) {
            ratingService.rate(session,
                    new RatingImpl(entry.getValue(),inArticle.getId(),entry.getKey(),""));
        }

    }

    /*
     * SEE nuxeo-data-demo, this comes from there. NO CONTROLS, NO CHECKS, JUST A RAW-SET OF THE VALUE. USE WITH CAUTION
     */
    protected void directSetCurrentLifecycleState(CoreSession inSession, DocumentModel inDoc, String inState)
            throws DocumentNotFoundException {

        LocalSession localSession = (LocalSession) inSession;
        Session baseSession = localSession.getSession();

        Document baseDoc = baseSession.getDocumentByUUID(inDoc.getId());
        // SQLDocument sqlDoc = (SQLDocument) baseDoc;
        // sqlDoc.setCurrentLifeCycleState(inState);
        baseDoc.setCurrentLifeCycleState(inState);

    }

}
