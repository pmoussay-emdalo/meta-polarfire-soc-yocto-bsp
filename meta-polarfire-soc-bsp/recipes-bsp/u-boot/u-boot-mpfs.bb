require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc
require u-boot-env-mpfs.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

PV = "2023.07+git${SRCPV}"
SRCREV = "60a6e2bd7d52e1ac7443839d1824433913273204"
SRC_URI = "git://github.com/polarfire-soc/u-boot.git;protocol=https;nobranch=1  \
           file://${HSS_PAYLOAD}.yaml \
           "

SRC_URI:append:icicle-kit = "file://${UBOOT_ENV}.cmd \
                             file://${MACHINE}.cfg \
                             file://uEnv.txt \
                            "

SRC_URI:append:icicle-kit-es-amp = "file://${UBOOT_ENV}.cmd \
                                    file://${MACHINE}.cfg \
                                    file://uEnv.txt \
                                "

SRC_URI:append:mpfs-video-kit = "file://${UBOOT_ENV}.cmd \
                                 file://${MACHINE}.cfg \
                                 file://uEnv.txt \
                                "

SRC_URI:append:mpfs-disco-kit = "file://${UBOOT_ENV}.cmd \
                                file://${MACHINE}.cfg \
                                file://uEnv.txt \
                                "

DEPENDS += " python3-setuptools-native u-boot-mkenvimage-native"
DEPENDS:append = " u-boot-tools-native hss-payload-generator-native"
DEPENDS:append:icicle-kit-es-amp = " polarfire-soc-amp-examples mpfs-zephyr-amp-demo"

do_deploy:append () {

    #
    # for icicle-kit-es-amp, we'll already have an amp-application.elf in
    # DEPLOY_DIR_IMAGE, so smuggle it in here for the payload generator ...
    #
    if [ ${HSS_PAYLOAD} == "amp" ]; then
        if [ ${AMP_DEMO} == "zephyr" ]; then
            cp ${DEPLOY_DIR_IMAGE}/zephyr-amp-application.elf ${DEPLOYDIR}/amp-application.elf
        elif [ -f "${DEPLOY_DIR_IMAGE}/amp-application.elf" ]; then
            cp -f ${DEPLOY_DIR_IMAGE}/amp-application.elf ${DEPLOYDIR}/amp-application.elf
        fi
        sed -i "s/\${AMP_DEMO}/${AMP_DEMO^}/g" ${WORKDIR}/${HSS_PAYLOAD}.yaml
    fi

    hss-payload-generator -c ${WORKDIR}/${HSS_PAYLOAD}.yaml -v ${DEPLOYDIR}/payload.bin

    #
    # for icicle-kit-es-amp, if we smuggled in an amp-application.elf, then
    # clean-up here before the Yocto framework gets angry that we're trying to install
    # files (from DEPLOYDIR) into a shared area (DEPLOY_IMAGE_DIR) when they already
    # exist
    #
    if [ -f "${DEPLOYDIR}/amp-application.elf" ]; then
        rm -f ${DEPLOYDIR}/amp-application.elf
    fi

}

COMPATIBLE_MACHINE = "(icicle-kit|mpfs-video-kit|mpfs-disco-kit)"
