/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.services.ecm.dms.metadata;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.exoplatform.services.cms.BasePath;
import org.exoplatform.services.cms.metadata.MetadataService;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.wcm.BaseWCMTestCase;

/**
 * Created by The eXo Platform SARL
 * June 09, 2009
 */
public class TestMetadataService extends BaseWCMTestCase {

  private MetadataService metadataService;
  private String expectedArticleDialogPath = "/exo:ecm/metadata/exo:article/dialogs/dialog1";
  private NodeHierarchyCreator nodeHierarchyCreator;
  private String baseMetadataPath;
  private Session sessionDMS;

  static private final String EXO_ARTICLE = "exo:article";
  
  public void setUp() throws Exception {
    super.setUp();
    metadataService = (MetadataService)container.getComponentInstanceOfType(MetadataService.class);
    nodeHierarchyCreator = (NodeHierarchyCreator)container.getComponentInstanceOfType(NodeHierarchyCreator.class);
    baseMetadataPath = nodeHierarchyCreator.getJcrPath(BasePath.METADATA_PATH);
    applySystemSession();
    sessionDMS = sessionProviderService_.getSystemSessionProvider(null).getSession(DMSSYSTEM_WS, repository);    
  }

  /**
   * Test MetadataServiceImpl.init()
   * Check all data initiated from repository in test-metadata-configuration.xml file
   * @throws Exception
   */
  public void testInit() throws Exception {
    metadataService.init();
  }

  /**
   * Test method: MetadataServiceImpl.getMetadataList()
   * Input: repository      String
   *                        The name of repository
   * Expect: Return name of all NodeType in repository
   * @throws Exception
   */
  public void testGetMetadataList() throws Exception {
    List<String> metadataTypes = metadataService.getMetadataList();
    assertTrue(metadataTypes.contains("dc:elementSet"));
  }

  /**
   * Test method: MetadataServiceImpl.getAllMetadatasNodeType()
   * Input: repository      String
   *                        The name of repository
   * Expect: Return all NodeType in repository with NodeType = exo:metadata
   * @throws Exception
   */
  public void testGetAllMetadatasNodeType() throws Exception {
    List<NodeType> metadataTypes = metadataService.getAllMetadatasNodeType();
    assertNotNull(metadataTypes.size());
  }

  /**
   * Test method: MetadataServiceImpl.addMetadata()
   * Input: nodetype    Node name for processing
   *        isDialog    true for dialog template
   *        role        permission
   *        content     content of template
   *        isAddNew    false if nodetype exist in repository, true if not
   *        repository  repository name
   * Expect: Add new nodetype and set property  EXO_ROLES_PROP, EXO_TEMPLATE_FILE_PROP
   * @throws Exception
   */
  public void testAddMetadata() throws Exception {
    metadataService.addMetadata(EXO_ARTICLE, true, "*:/platform/administrators;*:/platform/users", "This is content", true);

    Node myMetadata = (Node)sessionDMS.getItem("/exo:ecm/metadata/exo:article/dialogs/dialog1");
    assertEquals("This is content", myMetadata.getNode("jcr:content").getProperty("jcr:data").getString());

    String roles = metadataService.getMetadataRoles(EXO_ARTICLE, true);
    assertEquals("*:/platform/administrators; *:/platform/users", roles);
  }

  /**
   * Test method: MetadataServiceImpl.getMetadataTemplate()
   * Input: name            Node name
   *        isDialog        true: Get dialog template content
   *                        false: Get view template content
   *        repository      repository name
   * Expect: Return "This is content" is content of dialog template node or view template in repository
   * @throws Exception
   */
  public void testGetMetadataTemplate() throws Exception {
    metadataService.addMetadata(EXO_ARTICLE, true, "*:/platform/administrators", "This is content", true);
    assertEquals("This is content", metadataService.getMetadataTemplate(EXO_ARTICLE, true));
  }

  /**
   * Test method: MetadataServiceImpl.removeMetadata()
   * Input: nodetype      name of node
   *        repository    repository name
   * Expect: Remove metadata
   * @throws Exception
   */
  public void testRemoveMetadata() throws Exception {
    metadataService.addMetadata(EXO_ARTICLE, true, "*:/platform/administrators", "This is content", true);
    assertEquals("This is content", metadataService.getMetadataTemplate(EXO_ARTICLE, true));
    metadataService.removeMetadata(EXO_ARTICLE);
    assertNull(metadataService.getMetadataTemplate(EXO_ARTICLE, true));
  }

  /**
   * Test method: MetadataServiceImpl.getExternalMetadataType()
   * Input: repository    String
   *                      The name of repository
   * Expect: Remove metadata
   * @throws Exception
   */
  public void testGetExternalMetadataType() throws Exception {
    List<String> extenalMetaTypes = metadataService.getExternalMetadataType();
    assertEquals(2, extenalMetaTypes.size());
  }

  /**
   * Test method: MetadataServiceImpl.getMetadataPath()
   * Input: name            Node name
   *        isDialog        true: Get dialog template content
   *                        false: Get view template content
   *        repository      repository name
   * Expect: Return "/exo:ecm/metadata/exo:article/dialogs/dialog1" is path of dialog template or view tempate
   * @throws Exception
   */
  public void testGetMetadataPath() throws Exception {
    metadataService.addMetadata(EXO_ARTICLE, true, "*:/platform/administrators", "This is my content", true);
    assertEquals(expectedArticleDialogPath, metadataService.getMetadataPath(EXO_ARTICLE, true));
  }

  /**
   * Test method: MetadataServiceImpl.getMetadataRoles()
   * Input: name            Node name
   *        isDialog        true: Get dialog template content
   *                        false: Get view template content
   *        repository      repository name
   * Expect: Return "/exo:ecm/metadata/exo:article/dialogs/dialog1" is path of dialog template or view tempate
   * @throws Exception
   */
  public void testGetMetadataRoles() throws Exception {
    metadataService.addMetadata(EXO_ARTICLE, true, "*:/platform/administrators", "This is content", true);
    assertEquals("*:/platform/administrators", metadataService.getMetadataRoles(EXO_ARTICLE, true));
  }

  /**
   * Test method: MetadataServiceImpl.hasMetadata()
   * Input: name            Node name
   *        repository      repository name
   * Expect: true : Exist this node name
   *         false: Not exist this node name
   * @throws Exception
   */
  public void testHasMetadata() throws Exception {
    metadataService.addMetadata(EXO_ARTICLE, true, "*:/platform/administrators", "This is content", true);
    assertTrue(metadataService.hasMetadata(EXO_ARTICLE));
    metadataService.removeMetadata(EXO_ARTICLE);
    assertFalse(metadataService.hasMetadata(EXO_ARTICLE));
  }
  
  /**
   * Test method: MetadataServiceImpl.getMetadata(String metaName)
   * Input: metaName            Name of Metadata 
   * Expect: Return a Metadata node based on its name
   * @throws Exception
   */
  public void testGetMetadata() throws Exception {
    metadataService.addMetadata(EXO_ARTICLE, true, "*:/platform/administrators", "This is content", true);
    Node node = metadataService.getMetadata(EXO_ARTICLE);
    assertEquals(node.getName(), EXO_ARTICLE);
  }
  
  /**
   * Test method: MetadataServiceImpl.getMetadataLabel(String metaName)
   * Input: metaName            Name of Metadata
   * Expect: Return the label of Metadata node based on its name
   * @throws Exception
   */
  public void testGetMetadataLabel() throws Exception {
    metadataService.addMetadata(EXO_ARTICLE, true, "*:/platform/administrators", "This is content", true);
    Node node = metadataService.getMetadata(EXO_ARTICLE);
    node.setProperty("label", "Article metadata label");
    assertEquals("Article metadata label", metadataService.getMetadataLabel(EXO_ARTICLE));
  }

  /**
   * Clean all metadata test node
   */
  public void tearDown() throws Exception {
    Node myMetadata = (Node)sessionDMS.getItem(baseMetadataPath);
    if (myMetadata.hasNode("exo:article")) myMetadata.getNode("exo:article").remove();
    sessionDMS.save();
    super.tearDown();
  }
}
