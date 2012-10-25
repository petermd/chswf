chswf - simple swf modification
===============================

chswf if a java tool for making changes to a [swf file]. 

it only has one use at the moment, to add or remove the tag needed to enable advanced telemetry in the swf so that you can access detailed profiling information in [Monocle].

  [swf file]: http://www.adobe.com/devnet/swf.html
  [Monocle]: http://blogs.adobe.com/mallika/2012/10/introducing-project-monocle.html

## features ##

- read/write swf files
- support compressed swf files (read and write)
- add/remove tags

## usage ##
you can run it on the command-line or inside ant as follows

### command-line ###

add telemetry

    java -jar chswf-1.0.jar -telemetry=true SWF

dump tags

    java -jar chswf-1.0.jar -verbose SWF

### ANT ###

you need to include the chswf jar in the ANT classpath

#### make macro ####

    <macrodef name="chswf">
        <attribute name="file"/>
        <attribute name="telemetry" default="true"/>
        <attribute name="verbose" default="false"/>
        <sequential>
            <java classname="swf.ChangeSwf" fork="true" failonerror="true">
                <arg value="-telemetry=@{telemetry}"/>
                <arg value="-verbose=@{verbose}"/>
                <arg value="@{file}"/>
            </java>
        </sequential>
    </macrodef>

#### add telemetry to swf ####

     <chswf telemetry="true" file="swf-file"/>

