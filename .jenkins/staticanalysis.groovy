#!/usr/bin/env groovy
// This shared library is available at https://github.com/ROCmSoftwarePlatform/rocJENKINS/
@Library('rocJenkins@pong') _

// This is file for AMD Continuous Integration use.
// If you are interested in running your own Jenkins, please raise a github issue for assistance.

import com.amd.project.*
import com.amd.docker.*
import java.nio.file.Path

def runCI =
{
    nodeDetails, jobName->

    def prj  = new rocProject('rocBLAS-Examples', 'StaticAnalysis')

    def nodes = new dockerNodes(nodeDetails, jobName, prj)

    boolean formatCheck = true
    boolean staticAnalysis = true

    buildProject(prj, formatCheck, nodes.dockerArray, null, null, null, staticAnalysis)
}

ci: {
    String urlJobName = auxiliary.getTopJobName(env.BUILD_URL)

    properties(auxiliary.addCommonProperties([pipelineTriggers([cron('0 1 * * 6')])]))

    def jobNameList = ["main":([ubuntu22:['any']])]
    jobNameList = auxiliary.appendJobNameList(jobNameList, 'rocBLAS-Examples')

    jobNameList.each
    {
        jobName, nodeDetails->
        if (urlJobName == jobName)
            stage(jobName) {
                runCI(nodeDetails, jobName)
            }
    }
}
