SUMMARY = "Polarfire SoC blinky exemple using openamp"
DESCRIPTION = "Polarfire SoC blinky exemple with openamp"

require recipes-kernel/zephyr-kernel/zephyr-sample.inc

LICENSE="CLOSED"
LIC_FILE_CHKSUM=""

SRCREV_zephyr-applications = "779cd78b71c1a6ab01153c951e2f0943018872eb"
SRC_URI_APP = "git://github.com/pmoussay-emdalo/zephyr-applications;protocol=https;subpath=apps/amp_example"
SRC_URI:append = " ${SRC_URI_APP};name=zephyr-applications;nobranch=1;"

# Substitute HAL_MICROCHIP with our fork
SRCREV_hal_microchip = "0746d922bd0be125c1f20e2d31cff8c6b5a934b9"
SRC_URI_ZEPHYR_HAL_MICROCHIP = "git://github.com/pmoussay-emdalo/hal_microchip;protocol=https"
#SRC_URI:append = " ${SRC_URI_ZEPHYR_HAL_MICROCHIP};name=hal_microchip;nobranch=1;destsuffix=git/modules/hal/microchip "

SRCREV_rpmsg-lite = "7fc94efb2d8dbfa3d7c9212bcbb498fd3503af7a"
SRC_URI_ZEPHYR_RMSG_LITE = "git://github.com/pmoussay-emdalo/rpmsg-lite;protocol=https"
SRC_URI:append = " ${SRC_URI_ZEPHYR_RMSG_LITE};name=rpmsg-lite;nobranch=1;destsuffix=git/modules/lib/rpmsg-lite "

SRC_URI_PATCHES:append= "\
    file://0001-zephyr.patch;patchdir=zephyr \
"

# S = "${WORKDIR}/git"

ZEPHYR_SRC_DIR = "${WORKDIR}/amp_example"

ZEPHYR_MODULES:append = "${S}/modules/lib/rpmsg-lite\;"

ALLOWED_AMP_DEMO = "zephyr"

do_install() {
    if [[ "${ALLOWED_AMP_DEMO}" =~ "${AMP_DEMO}" ]]; then
        install -d ${D}${nonarch_base_libdir}/firmware
        install -D ${B}/zephyr/${ZEPHYR_MAKE_OUTPUT} ${D}${nonarch_base_libdir}/firmware/rproc-miv-rproc-fw
    else
        bbnote "${PN} do_install() have been skipped, because ${AMP_DEMO} is not covered by this recipe"
    fi
}

do_deploy() {
    cp ${B}/zephyr/${ZEPHYR_MAKE_OUTPUT} ${DEPLOYDIR}/zephyr-amp-application.elf
}

FILES:${PN} += "/lib/firmware/"
SYSROOT_DIRS += "/lib/firmware"
INSANE_SKIP += "ldflags buildpaths"

addtask deploy after do_install

COMPATIBLE_MACHINE = "(icicle-kit-es-amp)"
