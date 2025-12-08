import { v4 as uuidv4 } from 'uuid';

const DEVICE_ID_KEY = 'deviceId';

/**
 * Get or create Device ID from LocalStorage
 * Device ID is used to identify users without login
 */
export function getDeviceId(): string {
  // Check if we're in browser environment (Next.js SSR compatibility)
  if (typeof window === 'undefined') {
    return '';
  }

  try {
    // Try to get existing device ID from LocalStorage
    let deviceId = localStorage.getItem(DEVICE_ID_KEY);

    // If no device ID exists, generate a new one
    if (!deviceId) {
      deviceId = uuidv4();
      localStorage.setItem(DEVICE_ID_KEY, deviceId);
    }

    return deviceId;
  } catch (error) {
    console.error('Failed to get/set device ID:', error);
    // Return empty string if localStorage is not available
    return '';
  }
}

/**
 * Clear device ID from LocalStorage (for testing purposes)
 */
export function clearDeviceId(): void {
  if (typeof window !== 'undefined') {
    try {
      localStorage.removeItem(DEVICE_ID_KEY);
    } catch (error) {
      console.error('Failed to clear device ID:', error);
    }
  }
}
