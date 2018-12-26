package com.qaprosoft.jenkins.jobdsl.factory.pipeline

@Grab('org.testng:testng:6.8.8')

import org.testng.xml.Parser
import org.testng.xml.XmlSuite
import groovy.transform.InheritConstructors

@InheritConstructors
public class CronJobFactory extends PipelineFactory {

    def project
    def suitePath

    public CronJobFactory(folder, pipelineScript, cronJobName, project, suitePath, jobDesc) {
        this.folder = folder
		this.pipelineScript = pipelineScript
        this.description = jobDesc
        this.name = cronJobName
        this.project = project
        this.suitePath = suitePath
    }

    def create() {

        def xmlFile = new Parser(suitePath)
        xmlFile.setLoadClasses(false)

        List<XmlSuite> suiteXml = xmlFile.parseToList()
        XmlSuite currentSuite = suiteXml.get(0)

        def pipelineJob = super.create()

        pipelineJob.with {

            parameters {
                choiceParam('env', getEnvironments(currentSuite), 'Environment to test against.')
                configure addHiddenParameter('project', '', project)
                configure addHiddenParameter('ci_parent_url', '', '')
                configure addHiddenParameter('ci_parent_build', '', '')
                configure addExtensibleChoice('branch', "gc_GIT_BRANCH", "Select a GitHub Testing Repository Branch to run against", "master")
                stringParam('email_list', '', 'List of Users to be emailed after the test. If empty then populate from jenkinsEmail suite property')
                choiceParam('BuildPriority', [5, 4, 3, 2, 1], "Priority of execution. Lower number means higher priority")
                choiceParam('retry_count', [0, 1, 2, 3], 'Number of Times to Retry a Failed Test')
            }
        }
        return pipelineJob
    }

}